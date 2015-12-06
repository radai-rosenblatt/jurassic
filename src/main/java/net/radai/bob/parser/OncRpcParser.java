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

import net.radai.bob.grammar.oncrpcv2Lexer;
import net.radai.bob.grammar.oncrpcv2Parser;
import net.radai.bob.model.*;
import net.radai.bob.model.rpc.RpcProcedure;
import net.radai.bob.model.rpc.RpcProgram;
import net.radai.bob.model.rpc.RpcProgramVersion;
import net.radai.bob.model.xdr.*;
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

    public Namespace parse(Reader reader, String name) throws IOException {
        Namespace result = new Namespace(name);
        return parse(reader, result);
    }

    public Namespace parse(Reader reader, Namespace resultsAccumulator) throws IOException {
        if (resultsAccumulator == null) {
            throw new IllegalArgumentException("namespace argument cannot be null");
        }
        ValidatingErrorListener errorListener = new ValidatingErrorListener();
        ANTLRInputStream input = new ANTLRInputStream(reader);
        oncrpcv2Lexer lexer = new oncrpcv2Lexer(input);
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        oncrpcv2Parser parser = new oncrpcv2Parser(tokens);
        parser.addErrorListener(errorListener);
        oncrpcv2Parser.Oncrpcv2SpecificationContext tree = parser.oncrpcv2Specification();

        for (oncrpcv2Parser.XdrSpecificationContext xdrSpec : tree.xdrSpecification()) {
            for (oncrpcv2Parser.DefinitionContext definition : xdrSpec.definition()) {
                if (definition.typeDef() != null) {
                    //top-level type
                    XdrDeclaration type = parseTypedef(definition.typeDef(), resultsAccumulator);
                    resultsAccumulator.register(type);
                } else {
                    //constant (these are always top level)
                    XdrConstant constant = parseConstant(definition.constantDef());
                    resultsAccumulator.register(constant);
                }
            }
        }
        for (oncrpcv2Parser.ProgramDefContext programDef : tree.programDef()) {
            RpcProgram program = parseProgramDef(programDef, resultsAccumulator);
            resultsAccumulator.register(program);
        }

        return resultsAccumulator;
    }

    private XdrConstant parseConstant(oncrpcv2Parser.ConstantDefContext ctx) {
        String identifier = ctx.IDENTIFIER().getText();
        TerminalNode constantNode = AntlrUtil.resolveToTerminal(ctx.constant());
        BigInteger value = parseInteger(constantNode.getSymbol());
        return new XdrConstant(identifier, value);
    }

    private static BigInteger parseInteger(Token token) {
        String text = token.getText();
        switch (token.getType()) {
            case oncrpcv2Parser.DECIMAL:
                return new BigInteger(text);
            case oncrpcv2Parser.HEXADECIMAL:
                return new BigInteger(text.substring(2), 16); //drop "0x" prefix
            case oncrpcv2Parser.OCTAL:
                return new BigInteger(text.substring(1), 8); //drop "0" prefix
            default:
                throw new IllegalArgumentException("unable to parse integer from token: " + token
                        + " of type " + oncrpcv2Lexer.VOCABULARY.getDisplayName(token.getType()));
        }
    }

    private XdrDeclaration parseTypedef(oncrpcv2Parser.TypeDefContext ctx, Scope scope) {
        String firstWord = ctx.getChild(0).getText();
        XdrDeclaration declaration;
        if ("typedef".equals(firstWord)) {
            declaration = parseDeclaration(ctx.declaration(), scope);
        } else {
            declaration = new XdrDeclaration();
            declaration.setIdentifier(ctx.IDENTIFIER().getText());
            switch (firstWord) {
                case "enum":
                    declaration.setType(parseEnumType(ctx.enumBody(), scope));
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

    private XdrDeclaration parseDeclaration(oncrpcv2Parser.DeclarationContext ctx, Scope parentScope) {
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
            declaration.setSizeLimit(parseValue(ctx.value(), parentScope));
        }
        return declaration;
    }

    private XdrValue parseValue(oncrpcv2Parser.ValueContext ctx, Scope parentScope) {
        if (ctx.constant() != null) {
            BigInteger sizeLimit = parseInteger(AntlrUtil.resolveToTerminal(ctx.constant()).getSymbol());
            return new XdrConstantValue(sizeLimit);
        } else {
            return new XdrRefValue(ctx.IDENTIFIER().getText(), parentScope);
        }
    }

    private XdrType parseType(oncrpcv2Parser.TypeSpecifierContext ctx, Scope parentScope) {
        if (ctx.enumTypeSpec() != null) {
            return parseEnumType(ctx.enumTypeSpec().enumBody(), parentScope);
        }
        if (ctx.structTypeSpec() != null) {
            return parseStructType(ctx.structTypeSpec().structBody(), parentScope);
        }
        if (ctx.unionTypeSpec() != null) {
            return parseUnionType(ctx.unionTypeSpec().unionBody(), parentScope);
        }
        if (ctx.IDENTIFIER() != null) {
            return new XdrRefType(ctx.IDENTIFIER().getText(), parentScope);
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

    private XdrEnumType parseEnumType(oncrpcv2Parser.EnumBodyContext ctx, Scope parentScope) {
        XdrEnumType result = new XdrEnumType();
        List<TerminalNode> identifiers = ctx.IDENTIFIER();
        List<oncrpcv2Parser.ValueContext> values = ctx.value();
        for (int i = 0; i < identifiers.size(); i++) {
            result.add(identifiers.get(i).getText(), parseValue(values.get(i), parentScope));
        }
        return result;
    }

    private XdrStructType parseStructType(oncrpcv2Parser.StructBodyContext ctx, Scope parentScope) {
        XdrStructType result = new XdrStructType(parentScope);
        for (oncrpcv2Parser.DeclarationContext declaration : ctx.declaration()) {
            XdrDeclaration field = parseDeclaration(declaration, result);
            result.addField(field);
        }
        return result;
    }

    private XdrUnionType parseUnionType(oncrpcv2Parser.UnionBodyContext ctx, Scope parentScope) {
        List<oncrpcv2Parser.DeclarationContext> declarations = ctx.declaration(); //1 or 2
        XdrUnionType result = new XdrUnionType(parentScope);
        result.setDiscriminant(parseDeclaration(declarations.get(0), result));
        if (declarations.size() > 1) {
            //has a default arm
            result.add(null, parseDeclaration(declarations.get(1), result));
        }
        for (oncrpcv2Parser.CaseSpecContext arm : ctx.caseSpec()) {
            HashSet<XdrValue> caseValues = new HashSet<>();
            for (oncrpcv2Parser.ValueContext value : arm.value()) {
                caseValues.add(parseValue(value, result));
            }
            result.add(caseValues, parseDeclaration(arm.declaration(), result));
        }
        return result;
    }

    private RpcProgram parseProgramDef(oncrpcv2Parser.ProgramDefContext ctx, Scope parentScope) {
        RpcProgram result = new RpcProgram(parentScope);
        result.setName(ctx.IDENTIFIER().getText());
        result.setProgramNumber(parseInteger(AntlrUtil.resolveToTerminal(ctx.constant()).getSymbol()));
        for (oncrpcv2Parser.VersionDefContext versionDef : ctx.versionDef()) {
            RpcProgramVersion version = parseProgramVersionDef(versionDef, result);
            result.add(version);
        }
        return result;
    }

    private RpcProgramVersion parseProgramVersionDef(oncrpcv2Parser.VersionDefContext ctx, Scope parentScope) {
        RpcProgramVersion result = new RpcProgramVersion(parentScope);
        result.setIdentifier(ctx.IDENTIFIER().getText());
        result.setVersionNumber(parseInteger(AntlrUtil.resolveToTerminal(ctx.constant()).getSymbol()));
        for (oncrpcv2Parser.ProcedureDefContext procedureDef : ctx.procedureDef()) {
            RpcProcedure procedure = parseProcedureDef(procedureDef, result);
            result.add(procedure);
        }
        return result;
    }

    private RpcProcedure parseProcedureDef(oncrpcv2Parser.ProcedureDefContext ctx, Scope parentScope) {
        RpcProcedure result = new RpcProcedure();
        result.setName(ctx.IDENTIFIER().getText());
        result.setProcedureNumber(parseInteger(AntlrUtil.resolveToTerminal(ctx.constant()).getSymbol()));

        oncrpcv2Parser.ProcReturnContext returnTypeContext = ctx.procReturn();
        if (returnTypeContext.typeSpecifier() != null) {
            result.setReturnType(parseType(returnTypeContext.typeSpecifier(), parentScope));
        } else {
            result.setReturnType(XdrBasicType.VOID);
        }

        oncrpcv2Parser.ProcFirstArgContext firstArg = ctx.procFirstArg();
        if (firstArg.typeSpecifier() != null) {
            //method has at least one arg
            XdrType arg = parseType(firstArg.typeSpecifier(), null);
            result.add(arg);
            if (ctx.typeSpecifier() != null) {
                //method has multiple args
                for (oncrpcv2Parser.TypeSpecifierContext typeSpecifier : ctx.typeSpecifier()) {
                    arg = parseType(typeSpecifier, null);
                    result.add(arg);
                }
            }
        }

        return result;
    }
}
