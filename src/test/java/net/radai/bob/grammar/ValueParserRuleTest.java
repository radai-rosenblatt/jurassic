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
public class ValueParserRuleTest extends AbstractParserRuleTest {

    @Test
    public void testDecimal() throws Exception {
        assertCorrect("666");
    }

    @Test
    public void testNegative() throws Exception {
        assertCorrect("-6");
    }

    @Test
    public void testOctal() throws Exception {
        assertCorrect("06");
    }

    @Test
    public void testIdentifier() throws Exception {
        assertCorrect("bob_");
    }

    @Test
    public void testHexadecimal() throws Exception {
        assertCorrect("0xb0b");
    }
}
