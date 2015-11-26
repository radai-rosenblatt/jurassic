package net.radai.bob.codegen;

import net.radai.bob.model.Namespace;
import net.radai.bob.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by Radai Rosenblatt
 */
public class OncRpcGenTest {

    @Test
    public void testConstsGeneration() throws Exception {
        Namespace namespace = Util.parse(
                "const c1 = 1;\n"
              + "const c2 = 2147483647;\n" //int max
              + "const c3 = 2147483648;\n" //int max + 1
              + "const c4 = 9223372036854775807;\n" //long max
              + "const c5 = 9223372036854775808;\n" //long max + 1
        );
        OncRpcGen gen = new OncRpcGen();
        Map<Path, String> result = gen.generate(namespace);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        String constsFileName = namespace.getName() + "Consts";
        Path constsFilePath = Paths.get(constsFileName);
        Assert.assertTrue(result.containsKey(constsFilePath));
        String constsFileSource = result.get(constsFilePath);
        Assert.assertFalse(constsFileSource.isEmpty());

        Class<?> generatedClass = InMemoryJavaCompiler.compile(constsFileName, constsFileSource);
        Assert.assertEquals(5, generatedClass.getDeclaredFields().length);

        Assert.assertEquals(1, ReflectionTestUtils.getField(generatedClass, "c1"));
        Assert.assertEquals(Integer.MAX_VALUE, ReflectionTestUtils.getField(generatedClass, "c2"));
        Assert.assertEquals(Integer.MAX_VALUE + 1L, ReflectionTestUtils.getField(generatedClass, "c3"));
        Assert.assertEquals(Long.MAX_VALUE, ReflectionTestUtils.getField(generatedClass, "c4"));
        Assert.assertEquals(new BigInteger("9223372036854775808"), ReflectionTestUtils.getField(generatedClass, "c5"));
    }
}
