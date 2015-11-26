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

import net.radai.bob.model.Namespace;
import net.radai.bob.model.xdr.XdrConstant;
import net.radai.bob.util.Util;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * @author Radai Rosenblatt
 */
public class SimpleOncRpcParserTest {

    @Test
    public void testConstDefinition() throws Exception {
        Namespace results = Util.parse("const HUGE_CONST = 18446744073709551615;");
        XdrConstant parsed = results.getConstant("HUGE_CONST");
        Assert.assertNotNull(parsed);
        Assert.assertEquals("HUGE_CONST", parsed.getIdentifier());
        Assert.assertEquals(new BigInteger("18446744073709551615"), parsed.getValue());
    }

    @Test
    public void testConst() {
        Namespace results = Util.parse("const FALSE = 0; const TRUE = 1;");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoIdentifier() throws Exception {
        Namespace results = Util.parse("typedef void;");
    }

    @Test
    public void testUglyEnumDef() throws Exception {
        Namespace results = Util.parse("typedef enum { FALSE = 0, TRUE = 1 } bob;");
    }
}
