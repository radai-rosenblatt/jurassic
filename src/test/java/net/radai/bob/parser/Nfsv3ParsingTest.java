package net.radai.bob.parser;

import net.radai.bob.model.Namespace;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Radai Rosenblatt
 */
public class Nfsv3ParsingTest extends AbstractOncRpcParserTest {

    @Test
    public void testParsingNfsv3() throws Exception {
        Namespace namespace;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("nfs3.x")) {
            namespace = parse(new InputStreamReader(is));
        }
        //if we got here it means the parse didnt explode
    }
}
