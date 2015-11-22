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

package net.radai.bob.grammar;

import net.radai.bob.parser.ValidatingErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.junit.Assert;

import java.io.StringReader;
import java.util.Locale;

/**
 * @author Radai Rosenblatt
 */
public abstract class AbstractLexerRuleTest {

    protected Token parse(String expr) throws Exception {
        ValidatingErrorListener errorListener = new ValidatingErrorListener();
        ANTLRInputStream input = new ANTLRInputStream(new StringReader(expr));
        oncrpcv2Lexer lexer = new oncrpcv2Lexer(input);
        lexer.addErrorListener(errorListener);
        return lexer.nextToken();
    }

    protected String getRuleName() {
        String className = getClass().getSimpleName();
        int index = className.lastIndexOf("ParserRuleTest");
        if (index == -1) {
            throw new IllegalStateException("unable to parse rule name from " + className);
        }
        return className.substring(0, index).toUpperCase(Locale.ROOT);
    }

    protected void assertCorrect(String expr) throws Exception{
        Token t = parse(expr);
        String symbolicName = oncrpcv2Lexer.VOCABULARY.getSymbolicName(t.getType());
        Assert.assertEquals(getRuleName(), symbolicName);
    }

    protected void assertIncorrect(String expr) throws Exception{
        Token t;
        try {
            t = parse(expr);
        } catch (IllegalArgumentException e) {
            return; //thats a form of incorrect too
        }
        String symbolicName = oncrpcv2Lexer.VOCABULARY.getSymbolicName(t.getType());
        Assert.assertNotEquals(getRuleName(), symbolicName);
    }
}
