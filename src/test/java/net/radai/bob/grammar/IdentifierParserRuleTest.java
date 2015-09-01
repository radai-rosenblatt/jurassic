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

package net.radai.bob.grammar;

import org.junit.Test;

/**
 * @author Radai Rosenblatt
 */
public class IdentifierParserRuleTest extends AbstractLexerRuleTest {

    @Test
    public void testSimpleIdentifier() throws Exception {
        assertCorrect("bob_666");
    }

    @Test
    public void testSimpleIdentifierAndComments() throws Exception {
        assertCorrect("/* bob */ bob_666 /* charlie */");
    }

    @Test
    public void testDecimal() throws Exception {
        assertIncorrect("666");
    }

    @Test
    public void testHexadecimal() throws Exception {
        assertIncorrect("0x666");
    }

    @Test
    public void testStartsWithUnderscore() throws Exception {
        assertIncorrect("_bob");
    }
}
