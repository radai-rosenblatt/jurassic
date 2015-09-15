/*
 * This file is part of Bob.
 *
 * Bob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Bob. If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.bob.parser;

import net.radai.bob.grammar.ONCRPCv2Lexer;
import net.radai.bob.grammar.ONCRPCv2Parser;
import net.radai.bob.model.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

/**
 * @author Radai Rosenblatt
 */
public class OncRpcParser {

    public ResultsContainer parse(Reader reader) throws IOException {
        return parse(reader, null);
    }

    public ResultsContainer parse(Reader reader, ResultsContainer resultsAccumulator) throws IOException {
        ValidatingErrorListener errorListener = new ValidatingErrorListener();
        ANTLRInputStream input = new ANTLRInputStream(reader);
        ONCRPCv2Lexer lexer = new ONCRPCv2Lexer(input);
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ONCRPCv2Parser parser = new ONCRPCv2Parser(tokens);
        parser.addErrorListener(errorListener);
        ONCRPCv2Parser.Oncrpcv2SpecificationContext tree = parser.oncrpcv2Specification();

        ResultsContainer results = resultsAccumulator != null ? resultsAccumulator : new ResultsContainer();

        for (ONCRPCv2Parser.XdrSpecificationContext xdrSpec : tree.xdrSpecification()) {
            for (ONCRPCv2Parser.DefinitionContext definition : xdrSpec.definition()) {
                if (definition.typeDef() != null) {
                    //top-level type
                    XdrDeclaration type = parseTypedef(definition.typeDef(), results);
                    results.register(type);
                } else {
                    //constant (these are always top level)
                    XdrConstant constant = parseConstant(definition.constantDef());
                    results.register(constant);
                }
            }
        }
        for (ONCRPCv2Parser.ProgramDefContext programDef : tree.programDef()) {
            throw new UnsupportedOperationException("program defs not supported yet");
        }

        return results;
    }

    private XdrConstant parseConstant(ONCRPCv2Parser.ConstantDefContext ctx) {
        String identifier = ctx.IDENTIFIER().getText();
        TerminalNode constantNode = AntlrUtil.resolveToTerminal(ctx.constant());
        BigInteger value = parseInteger(constantNode.getSymbol());
        return new XdrConstant(identifier, value);
    }

    private BigInteger parseInteger(Token token) {
        String text = token.getText();
        switch (token.getType()) {
            case ONCRPCv2Parser.DECIMAL:
                return new BigInteger(text);
            case ONCRPCv2Parser.HEXADECIMAL:
                return new BigInteger(text.substring(2)); //drop "0x" prefix
            case ONCRPCv2Parser.OCTAL:
                return new BigInteger(text.substring(1)); //drop "0" prefix
            default:
                throw new IllegalArgumentException("unable to parse integer from token: " + token
                        + " of type " + ONCRPCv2Lexer.VOCABULARY.getDisplayName(token.getType()));
        }
    }

    private XdrDeclaration parseTypedef(ONCRPCv2Parser.TypeDefContext ctx, XdrScope scope) {
        String firstWord = ctx.getChild(0).getText();
        XdrDeclaration declaration;
        if ("typedef".equals(firstWord)) {
            declaration = parseDeclaration(ctx.declaration(), scope);
        } else {
            declaration = new XdrDeclaration();
            declaration.setIdentifier(ctx.IDENTIFIER().getText());
            switch (firstWord) {
                case "enum":
                    declaration.setType(parseEnumType(ctx.enumBody()));
                    break;
                case "struct":
                    declaration.setType(parseStructType(ctx.structBody(), scope));
                    break;
                case "union":
                    declaration.setType(parseUnionType(ctx.unionBody(), scope));
                    break;
                default:
                    throw new UnsupportedOperationException("unhandled: " + firstWord);
            }
        }

        //validate
        if (declaration.getIdentifier() == null) {
            int lineNumber = AntlrUtil.findFirstTerminal(ctx).getSymbol().getLine();
            throw new IllegalArgumentException("declaration at line " + lineNumber + " must have a name");
        }

        return declaration;
    }

    private XdrDeclaration parseDeclaration(ONCRPCv2Parser.DeclarationContext ctx, XdrScope parentScope) {
        XdrDeclaration declaration = new XdrDeclaration();
        ParseTree firstChild = ctx.getChild(0);
        if (firstChild instanceof TerminalNode) {
            //opaque, string or void
            TerminalNode keywordNode = (TerminalNode) firstChild;
            String firstWord = keywordNode.getSymbol().getText();
            switch (firstWord) {
                case "opaque":
                    declaration.setType(XdrBasicType.OPAQUE);
                    break;
                case "string":
                    declaration.setType(XdrBasicType.STRING);
                    break;
                case "void":
                    declaration.setType(XdrBasicType.VOID);
                    break;
                default:
                    throw new UnsupportedOperationException("unhandled: " + firstWord);
            }
        } else {
            declaration.setType(parseType(ctx.typeSpecifier(), parentScope));
        }
        if (AntlrUtil.hasChild(ctx, "*")) {
            //optional declaration
            declaration.setOptional(true);
        } else if (AntlrUtil.hasChild(ctx, "[")) {
            //fixed size array
            declaration.setArray(true);
            declaration.setFixedSize(true);
        } else if (AntlrUtil.hasChild(ctx, "<")) {
            //variable size array
            declaration.setArray(true);
            declaration.setFixedSize(false);
        }
        if (ctx.IDENTIFIER() != null) {
            //for everything except void
            declaration.setIdentifier(ctx.IDENTIFIER().getText());
        }
        if (ctx.value() != null) {
            declaration.setSizeLimit(parseValue(ctx.value()));
        }
        return declaration;
    }

    private XdrValue parseValue(ONCRPCv2Parser.ValueContext ctx) {
        if (ctx.constant() != null) {
            BigInteger sizeLimit = parseInteger(AntlrUtil.resolveToTerminal(ctx.constant()).getSymbol());
            return new XdrConstantValue(sizeLimit);
        } else {
            return new XdrRefValue(ctx.IDENTIFIER().getText());
        }
    }

    private XdrType parseType(ONCRPCv2Parser.TypeSpecifierContext ctx, XdrScope parentScope) {
        if (ctx.enumTypeSpec() != null) {
            return parseEnumType(ctx.enumTypeSpec().enumBody());
        }
        if (ctx.structTypeSpec() != null) {
            return parseStructType(ctx.structTypeSpec().structBody(), parentScope);
        }
        if (ctx.unionTypeSpec() != null) {
            return parseUnionType(ctx.unionTypeSpec().unionBody(), parentScope);
        }
        if (ctx.IDENTIFIER() != null) {
            return new XdrRefType(ctx.IDENTIFIER().getText());
        }
        String firstChild = ctx.getChild(0).getText();
        switch (firstChild) {
            case "int":
                return XdrBasicType.INT;
            case "hyper":
                return XdrBasicType.HYPER;
            case "unsigned":
                String secondChild = ctx.getChild(1).getText();
                if ("int".equals(secondChild)) {
                    return XdrBasicType.UNSIGNED_INT;
                }
                if ("hyper".equals(secondChild)) {
                    return XdrBasicType.UNSIGNED_HYPER;
                }
                throw new IllegalStateException("unexpected " + secondChild);
            case "float":
                return XdrBasicType.FLOAT;
            case "double":
                return XdrBasicType.DOUBLE;
            case "quadruple":
                return XdrBasicType.QUADRUPLE;
            case "bool":
                return XdrBasicType.BOOL;
            default:
                throw new IllegalStateException("unexpected " + firstChild);
        }
    }

    private XdrEnumType parseEnumType(ONCRPCv2Parser.EnumBodyContext ctx) {
        XdrEnumType result = new XdrEnumType();
        List<TerminalNode> identifiers = ctx.IDENTIFIER();
        List<ONCRPCv2Parser.ValueContext> values = ctx.value();
        for (int i = 0; i < identifiers.size(); i++) {
            result.add(identifiers.get(i).getText(), parseValue(values.get(i)));
        }
        return result;
    }

    private XdrStructType parseStructType(ONCRPCv2Parser.StructBodyContext ctx, XdrScope parentScope) {
        XdrStructType result = new XdrStructType(parentScope);
        for (ONCRPCv2Parser.DeclarationContext declaration : ctx.declaration()) {
            XdrDeclaration field = parseDeclaration(declaration, result);
            result.addField(field);
        }
        return result;
    }

    private XdrUnionType parseUnionType(ONCRPCv2Parser.UnionBodyContext ctx, XdrScope parentScope) {
        List<ONCRPCv2Parser.DeclarationContext> declarations = ctx.declaration(); //1 or 2
        XdrUnionType result = new XdrUnionType(parentScope);
        result.setDiscriminant(parseDeclaration(declarations.get(0), result));
        if (declarations.size() > 1) {
            //has a default arm
            result.addArm(null, parseDeclaration(declarations.get(1), result));
        }
        for (ONCRPCv2Parser.CaseSpecContext arm : ctx.caseSpec()) {
            HashSet<XdrValue> caseValues = new HashSet<>();
            for (ONCRPCv2Parser.ValueContext value : arm.value()) {
                caseValues.add(parseValue(value));
            }
            result.addArm(caseValues, parseDeclaration(arm.declaration(), result));
        }
        return result;
    }
}
