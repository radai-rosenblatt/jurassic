package net.radai.bob.codegen;

import net.radai.bob.model.xdr.XdrBasicType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Radai Rosenblatt
 */
public class XdrJavaLangUtil {

    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<>();
    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_ARRAYS = new HashMap<>();

    static {
        //only those useful for xdr
        PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
        PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
        PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
        PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
        PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);

        PRIMITIVES_TO_ARRAYS.put(boolean.class, boolean[].class);
        PRIMITIVES_TO_ARRAYS.put(double.class, double[].class);
        PRIMITIVES_TO_ARRAYS.put(float.class, float[].class);
        PRIMITIVES_TO_ARRAYS.put(int.class, int[].class);
        PRIMITIVES_TO_ARRAYS.put(long.class, long[].class);
    }

    public static Class<?> translateBasicType(XdrBasicType type) {
        switch (type.getType()) {
            case UNSIGNED_INT:
            case INT:
                return int.class;
            case UNSIGNED_HYPER:
            case HYPER:
                return long.class;
            case FLOAT:
                return float.class;
            case DOUBLE:
                return double.class;
            case QUADRUPLE:
                return BigDecimal.class;
            case BOOL:
                return boolean.class;
            case OPAQUE:
                return byte[].class;
            case STRING:
                return String.class;
            default:
                throw new IllegalStateException("unhandled xdr basic type " + type.getType());
        }
    }

    public static Class<?> optionalOf(Class<?> base) {
        if (!base.isPrimitive()) {
            return base; //not primitive, hence nullable.
        }
        Class<?> wrapperType = PRIMITIVES_TO_WRAPPERS.get(base);
        if (wrapperType == null) {
            throw new IllegalArgumentException("cant find the nullable wrapper type for " + base);
        }
        return wrapperType;
    }

    public static Class<?> arrayOf(Class<?> element) {
        if (element.isPrimitive()) {
            Class<?> arrayType = PRIMITIVES_TO_ARRAYS.get(element);
            if (arrayType == null) {
                throw new IllegalArgumentException("cant find the array type for " + element);
            }
            return arrayType;
        }
        try {
            if (element.isArray()) {
                return Class.forName("[" + element.getName());
            } else {
                return Class.forName("[L" + element.getCanonicalName() + ";");
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
