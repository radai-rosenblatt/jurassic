package net.radai.bob.runtime.wire;

import java.io.IOException;

/**
 * @author Radai Rosenblatt
 */
public interface XdrInput {
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
        boolean[] result = new boolean[ofSize];
        for (int i=0; i<ofSize; i++) {
            result[i] = readBoolean();
        }
        return result;
    }
    default boolean[] readVariableBooleanArray() throws IOException {
        return readFixedBooleanArray(readInt());
    }
}
