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

import net.radai.bob.model.Identifiable;
import net.radai.bob.model.Namespace;
import net.radai.bob.model.xdr.XdrDeclaration;
import net.radai.bob.model.xdr.XdrStructType;
import net.radai.bob.util.Util;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by Radai Rosenblatt
 */
public class Nfsv3ParsingTest {

    @Test
    public void testParsingNfsv3() throws Exception {
        Namespace namespace;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("nfs3.x")) {
            namespace = Util.parse(is);
        }
        XdrDeclaration fattr3 = (XdrDeclaration) namespace.resolve("fattr3");
        XdrStructType type = (XdrStructType) fattr3.getType();
        XdrDeclaration uidField = (XdrDeclaration) type.resolve("uid");
        XdrDeclaration effectiveDeclaration = uidField.getEffectiveDeclaration();
        int g = 7;
        //if we got here it means the parse didnt explode
    }
}
