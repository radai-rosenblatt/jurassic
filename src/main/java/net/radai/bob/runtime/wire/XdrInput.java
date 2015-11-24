package net.radai.bob.runtime.wire;

import java.io.IOException;

/**
 * @author Radai Rosenblatt
 */
public interface XdrInput {
    byte[] EMPTY_BYTE_ARRAY = new byte[0];
    boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    int[] EMPTY_INT_ARRAY = new int[0];
    long[] EMPTY_LONG_ARRAY = new long[0];
    float[] EMPTY_FLOAT_ARRAY = new float[0];
    double[] EMPTY_DOUBLE_ARRAY = new double[0];

    default boolean readBoolean() throws IOException {
        return readInt() != 0; //very lax
    }
    int readInt() throws IOException;
    long readLong() throws IOException;
    default float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }
    default double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }
    String readString() throws IOException;
    byte[] readFixedByteArray(int ofSize) throws IOException;
    default byte[] readVariableByteArray() throws IOException {
        return readFixedByteArray(readInt());
    }
    default boolean[] readFixedBooleanArray(int ofSize) throws IOException {
        if (ofSize == 0) {
            return EMPTY_BOOLEAN_ARRAY;
        }
        boolean[] result = new boolean[ofSize];
        for (int i=0; i<ofSize; i++) {
            result[i] = readBoolean();
        }
        return result;
    }
    default boolean[] readVariableBooleanArray() throws IOException {
        return readFixedBooleanArray(readInt());
    }
    default int[] readFixedIntArray(int ofSize) throws IOException {
        if (ofSize == 0) {
            return EMPTY_INT_ARRAY;
        }
        int[] result = new int[ofSize];
        for (int i=0; i<ofSize; i++) {
            result[i] = readInt();
        }
        return result;
    }
    default int[] readVariableIntArray() throws IOException {
        return readFixedIntArray(readInt());
    }
    default long[] readFixedLongArray(int ofSize) throws IOException {
        if (ofSize == 0) {
            return EMPTY_LONG_ARRAY;
        }
        long[] result = new long[ofSize];
        for (int i=0; i<ofSize; i++) {
            result[i] = readLong();
        }
        return result;
    }
    default long[] readVariableLongArray() throws IOException {
        return readFixedLongArray(readInt());
    }
    default float[] readFixedFloatArray(int ofSize) throws IOException {
        if (ofSize == 0) {
            return EMPTY_FLOAT_ARRAY;
        }
        float[] result = new float[ofSize];
        for (int i=0; i<ofSize; i++) {
            result[i] = readFloat();
        }
        return result;
    }
    default float[] readVariableFloatArray() throws IOException {
        return readFixedFloatArray(readInt());
    }
    default double[] readFixedDoubleArray(int ofSize) throws IOException {
        if (ofSize == 0) {
            return EMPTY_DOUBLE_ARRAY;
        }
        double[] result = new double[ofSize];
        for (int i=0; i<ofSize; i++) {
            result[i] = readDouble();
        }
        return result;
    }
    default double[] readVariableDoubleArray() throws IOException {
        return readFixedDoubleArray(readInt());
    }
}
