package net.radai.bob.parser;

import net.radai.bob.util.Util;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by Radai Rosenblatt
 */
public class Nfsv3ParsingTest {

    @Test
    public void testParsingNfsv3() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("nfs3.x")) {
            Util.parse(is);
        }
        //if we got here it means the parse didnt explode
    }
}
