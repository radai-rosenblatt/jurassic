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
public class DecimalParserRuleTest extends AbstractLexerRuleTest {

    @Test
    public void testBasicDecimal() throws Exception {
        assertCorrect("10");
    }

    @Test
    public void testZero() throws Exception {
        assertCorrect("0");
    }

    @Test
    public void testOctal() throws Exception {
        assertIncorrect("07");
    }

    @Test
    public void testNegative() throws Exception {
        assertCorrect("-1");
    }

    @Test
    public void testBasicHexadecimal() throws Exception {
        assertIncorrect("0xb0b");
    }
}
