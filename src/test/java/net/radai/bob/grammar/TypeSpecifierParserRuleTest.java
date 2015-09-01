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
public class TypeSpecifierParserRuleTest extends AbstractParserRuleTest {

    @Test
    public void testUnsignedInt() throws Exception {
        assertCorrect("unsigned  \t int");
    }

    @Test
    public void testInt() throws Exception {
        assertCorrect("int");
    }

    @Test
    public void testUnsignedHyper() throws Exception {
        assertCorrect("unsigned hyper");
    }

    @Test
    public void testHyper() throws Exception {
        assertCorrect("hyper");
    }

    @Test
    public void testFloat() throws Exception {
        assertCorrect("float");
    }

    @Test
    public void testDouble() throws Exception {
        assertCorrect("double");
    }

    @Test
    public void testQuadruple() throws Exception {
        assertCorrect("quadruple");
    }
}
