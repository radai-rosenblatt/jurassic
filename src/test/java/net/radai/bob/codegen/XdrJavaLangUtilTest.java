package net.radai.bob.codegen;

import net.radai.bob.model.xdr.XdrBasicType;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.PropertySource;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Radai Rosenblatt
 */
public class XdrJavaLangUtilTest {

    @Test
    public void testBasicTypeResolution() throws Exception {
        Class<?> type = XdrJavaLangUtil.translateBasicType(XdrBasicType.FLOAT);
        Assert.assertEquals(float.class, type);
        Class<?> nullableType = XdrJavaLangUtil.optionalOf(type);
        Assert.assertEquals(Float.class, nullableType);
        Class<?> arrayType = XdrJavaLangUtil.arrayOf(type);
        Assert.assertEquals(float[].class, arrayType);
        Class<?> arrayOfNullablesType = XdrJavaLangUtil.arrayOf(nullableType);
        Assert.assertTrue(arrayOfNullablesType.isArray());
        Assert.assertEquals(nullableType, arrayOfNullablesType.getComponentType());
        Class<?> twoDimensionalArrayType = XdrJavaLangUtil.arrayOf(arrayType);
        Assert.assertEquals(float[][].class, twoDimensionalArrayType);
        Class<?> twoDimensionalNullableArrayType = XdrJavaLangUtil.arrayOf(arrayOfNullablesType);
        Assert.assertEquals(Float[][].class, twoDimensionalNullableArrayType);
    }

    @Test
    @Ignore("https://issues.jboss.org/browse/ROASTER-88")
    public void testGeneratedTypes() throws Exception {
        JavaClassSource structClass = Roaster.create(JavaClassSource.class);
        structClass.setName("Bob");
        PropertySource<JavaClassSource> property = structClass.addProperty(int[].class, "blob");
    }
}
