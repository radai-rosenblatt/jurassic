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

import net.radai.bob.grammar.ONCRPCv2BaseListener;
import net.radai.bob.grammar.ONCRPCv2Lexer;
import net.radai.bob.grammar.ONCRPCv2Parser;
import net.radai.bob.model.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

/**
 * @author Radai Rosenblatt
 */
public class OncRpcTreeListener extends ONCRPCv2BaseListener {
    private final ResultsContainer results;

    public OncRpcTreeListener(ResultsContainer results) {
        this.results = results;
    }

    @Override
    public void enterConstantDef(ONCRPCv2Parser.ConstantDefContext def) {
        String constName = def.IDENTIFIER().getText();
        TerminalNode constantNode = AntlrUtil.resolveToTerminal(def.constant());
        BigInteger value = parseInteger(constantNode.getSymbol());
        results.register(new XdrConstant(constName, value));
    }

    @Override
    public void enterTypeDef(ONCRPCv2Parser.TypeDefContext ctx) {
        TerminalNode keywordNode = AntlrUtil.resolveToTerminal(ctx.getChild(0));
        String firstWord = keywordNode.getSymbol().getText();
        XdrDeclaration declaration;
        switch (firstWord) {
            case "typedef":
                declaration = parseDeclaration(ctx.declaration());
                break;
            case "enum":
            case "struct":
            case "union":
            default:
                throw new UnsupportedOperationException("unhandled: " + firstWord);
        }
        int g = 8;
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

    private XdrDeclaration parseDeclaration(ONCRPCv2Parser.DeclarationContext ctx) {
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
            declaration.setType(parseType(ctx.typeSpecifier()));
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

    public XdrValue parseValue(ONCRPCv2Parser.ValueContext ctx) {
        if (ctx.constant() != null) {
            BigInteger sizeLimit = parseInteger(AntlrUtil.resolveToTerminal(ctx.constant()).getSymbol());
            return new XdrConstantValue(sizeLimit);
        } else {
            return new XdrRefValue(ctx.IDENTIFIER().getText());
        }
    }

    public XdrType parseType(ONCRPCv2Parser.TypeSpecifierContext ctx) {
        if (ctx.enumTypeSpec() != null) {
            return parseEnumType(ctx.enumTypeSpec().enumBody());
        }
        if (ctx.structTypeSpec() != null) {
            return parseStructType(ctx.structTypeSpec().structBody());
        }
        if (ctx.unionTypeSpec() != null) {
            return parseUnionType(ctx.unionTypeSpec().unionBody());
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
            case "float" :
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

    public XdrEnumType parseEnumType(ONCRPCv2Parser.EnumBodyContext ctx) {
        XdrEnumType result = new XdrEnumType();
        List<TerminalNode> identifiers = ctx.IDENTIFIER();
        List<ONCRPCv2Parser.ValueContext> values = ctx.value();
        for (int i=0; i<identifiers.size(); i++) {
            result.add(identifiers.get(i).getText(), parseValue(values.get(i)));
        }
        return result;
    }

    public XdrStructType parseStructType(ONCRPCv2Parser.StructBodyContext ctx) {
        XdrStructType result = new XdrStructType();
        for (ONCRPCv2Parser.DeclarationContext declaration : ctx.declaration()) {
            XdrDeclaration field = parseDeclaration(declaration);
            result.addField(field);
        }
        return result;
    }

    public XdrUnionType parseUnionType(ONCRPCv2Parser.UnionBodyContext ctx) {
        List<ONCRPCv2Parser.DeclarationContext> declarations = ctx.declaration(); //1 or 2
        XdrUnionType result = new XdrUnionType(parseDeclaration(declarations.get(0)));
        if (declarations.size() > 1) {
            //has a default arm
            result.addArm(null, parseDeclaration(declarations.get(1)));
        }
        for (ONCRPCv2Parser.CaseSpecContext arm : ctx.caseSpec()) {
            HashSet<XdrValue> caseValues = new HashSet<>();
            for (ONCRPCv2Parser.ValueContext value : arm.value()) {
                caseValues.add(parseValue(value));
            }
            result.addArm(caseValues, parseDeclaration(arm.declaration()));
        }
        return result;
    }
}
