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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.bob.parser;

import net.radai.bob.grammar.ONCRPCv2BaseListener;
import net.radai.bob.grammar.ONCRPCv2Lexer;
import net.radai.bob.grammar.ONCRPCv2Parser;
import net.radai.bob.model.XdrConstant;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigInteger;

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
}
