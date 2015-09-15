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

import org.junit.Test;

/**
 * @author Radai Rosenblatt
 */
public class CommentLexerRuleTest extends AbstractLexerRuleTest {

    @Override
    protected String getRuleName() {
        return "EOF"; //comments are discarded, so the resulting token is eof
    }

    @Test
    public void testSimpleComment() throws Exception {
        assertCorrect("/* this is a simple comment */");
    }

    @Test
    public void testMultilineComment() throws Exception {
        assertCorrect("/** this \n \t * is \r\n a \r\n multiline *** comment **/");
    }

    @Test
    public void testNotAComment() throws Exception {
        assertIncorrect("bob");
    }
}
