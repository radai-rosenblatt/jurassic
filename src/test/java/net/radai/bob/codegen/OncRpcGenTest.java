package net.radai.bob.codegen;

import net.radai.bob.model.Namespace;
import net.radai.bob.model.xdr.XdrConstant;
import net.radai.bob.model.xdr.XdrDeclaration;
import net.radai.bob.util.Util;
import net.radai.compilib.Compilib;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by Radai Rosenblatt
 */
public class OncRpcGenTest {

    private OncRpcGen gen = new OncRpcGen();

    @Test
    public void testConstsGeneration() throws Exception {
        Namespace namespace = Util.parse(
                "const c1 = 1;\n"
              + "const c2 = 2147483647;\n" //int max
              + "const c3 = 2147483648;\n" //int max + 1
              + "const c4 = 9223372036854775807;\n" //long max
              + "const c5 = 9223372036854775808;\n" //long max + 1
              + "const c6 = 0xB0B;\n" //hex
              + "const c7 = 077;\n" //octal
        );
        Map<Path, String> result = gen.generate(namespace);
        Assert.assertEquals(1, result.size());

        String constsFileName = namespace.getName() + "Consts";
        Path constsFilePath = Paths.get(constsFileName);
        Assert.assertTrue(result.containsKey(constsFilePath));
        String constsFileSource = result.get(constsFilePath);
        Assert.assertFalse(constsFileSource.isEmpty());

        Class<?> generatedClass = Compilib.compile(constsFileSource);
        Assert.assertEquals(7, generatedClass.getDeclaredFields().length);

        Assert.assertEquals(1, ReflectionTestUtils.getField(generatedClass, "c1"));
        Assert.assertEquals(Integer.MAX_VALUE, ReflectionTestUtils.getField(generatedClass, "c2"));
        Assert.assertEquals(Integer.MAX_VALUE + 1L, ReflectionTestUtils.getField(generatedClass, "c3"));
        Assert.assertEquals(Long.MAX_VALUE, ReflectionTestUtils.getField(generatedClass, "c4"));
        Assert.assertEquals(new BigInteger("9223372036854775808"), ReflectionTestUtils.getField(generatedClass, "c5"));
        Assert.assertEquals(2827, ReflectionTestUtils.getField(generatedClass, "c6"));
        Assert.assertEquals(63, ReflectionTestUtils.getField(generatedClass, "c7"));
    }

    @Test
    public void testEnumGeneration() throws Exception {
        String[] variations = new String[] {
                "enum Bob {\n" +
                "    ALT1 = 0,\n" +
                "    ALT2 = 10,\n" +
                "    ALT3 = 0xB0B\n" +
                "};",
                "typedef enum {\n" +
                "    ALT1 = 0,\n" +
                "    ALT2 = 10,\n" +
                "    ALT3 = 0xB0B\n" +
                "} Bob;"
        };
        for (String xdr : variations) {
            Namespace namespace = Util.parse(xdr);
            Map<String, XdrDeclaration> types = namespace.getTypes();
            Assert.assertEquals(1, types.size());
            Assert.assertTrue(types.containsKey("Bob"));

            Map<Path, String> result = gen.generate(namespace);
            Assert.assertEquals(1, result.size());
            String source = result.get(Paths.get("Bob"));
            Assert.assertFalse(source.isEmpty());

            Class<?> generatedClass = Compilib.compile(source);
            Assert.assertTrue(generatedClass.isEnum());
            Assert.assertEquals(0, ReflectionTestUtils.invokeGetterMethod(generatedClass.getDeclaredField("ALT1").get(null), "value"));
            Assert.assertEquals(10, ReflectionTestUtils.invokeGetterMethod(generatedClass.getDeclaredField("ALT2").get(null), "value"));
            Assert.assertEquals(2827, ReflectionTestUtils.invokeGetterMethod(generatedClass.getDeclaredField("ALT3").get(null), "value"));
            //attempt to verify declaration order
            Assert.assertTrue(source.indexOf("ALT1") < source.indexOf("ALT2"));
            Assert.assertTrue(source.indexOf("ALT2") < source.indexOf("ALT3"));
        }
    }

    @Test
    public void testEnumConstCombo() throws Exception {
        String[] variations = new String[] {
                "const C = 1;\n" +
                "enum Bob {\n" +
                        "    ALT1 = C\n" +
                        "};",
                "enum Bob {\n" +
                "    ALT1 = C\n" +
                "};\n" +
                "const C = 1;\n" //TODO - not sure the XDR spec itself allows forward lookup ...
        };
        for (String xdr : variations) {
            Namespace namespace = Util.parse(xdr);
            Map<String, XdrDeclaration> types = namespace.getTypes();
            Assert.assertEquals(1, types.size());
            Assert.assertTrue(types.containsKey("Bob"));
            Map<String, XdrConstant> constants = namespace.getConstants();
            Assert.assertEquals(1, constants.size());
            Assert.assertTrue(constants.containsKey("C"));

            Map<Path, String> result = gen.generate(namespace);
            Assert.assertEquals(2, result.size());
            String enumSourse = result.get(Paths.get("Bob"));
            String constsSourse = result.get(Paths.get("TestConsts"));
            Assert.assertFalse(enumSourse.isEmpty());

            Map<String, Class<?>> compiled = Compilib.compile(result.values());
            Class<?> enumClass = compiled.get("Bob");
            Class<?> constClass = compiled.get("TestConsts");
            Assert.assertTrue(enumClass.isEnum());
            Assert.assertTrue(constClass.isInterface());
            Assert.assertEquals(1, ReflectionTestUtils.invokeGetterMethod(enumClass.getDeclaredField("ALT1").get(null), "value"));
        }
    }
}
