package net.radai.bob.runtime.wire;

import net.radai.bob.runtime.model.XdrSerializable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Collection;

/**
 * @author Radai Rosenblatt
 */
public interface XdrOutput {
    Charset ascii = Charset.forName("ASCII");

    int padZeroes(int numBytes) throws IOException;

    //primitives
    default int write(boolean data) throws IOException {
        return write(data ? 1 : 0);
    }
    int write(int data) throws IOException;
    int write(long data) throws IOException;
    default int write(float data) throws IOException {
        return write(Float.floatToIntBits(data));
    }
    default int write(double data) throws IOException {
        return write(Double.doubleToLongBits(data));
    }
    default int write(String data) throws IOException {
        ByteBuffer buffer = ascii.encode(data);
        return writeVariable(buffer.array());
    }
    default int write(XdrSerializable data) throws IOException {
        int written = data.serializeTo(this);
        int padding = written % 4;
        if (padding == 0) {
            return written;
        }
        return written + padZeroes(4 - padding);
    }

    //arrays
    int writeFixed(byte[] data) throws IOException;
    default int writeVariable(byte[] data) throws IOException {
        return write(data.length) + writeFixed(data);
    }
    default int writeFixed(boolean[] data) throws IOException {
        int written = 0;
        for (boolean b : data) {
            written += write(b);
        }
        return written;
    }
    default int writeVariable(boolean[] data) throws IOException {
        return write(data.length) + writeFixed(data);
    }
    default int writeFixed(int[] data) throws IOException {
        int written = 0;
        for (int i : data) {
            written += write(i);
        }
        return written;
    }
    default int writeVariable(int[] data) throws IOException {
        return write(data.length) + writeFixed(data);
    }
    default int writeFixed(long[] data) throws IOException {
        int written = 0;
        for (long l : data) {
            written += write(l);
        }
        return written;
    }
    default int writeVariable(long[] data) throws IOException {
        return write(data.length) + writeFixed(data);
    }
    default int writeFixed(float[] data) throws IOException {
        int written = 0;
        for (float f : data) {
            written += write(f);
        }
        return written;
    }
    default int writeVariable(float[] data) throws IOException {
        return write(data.length) + writeFixed(data);
    }
    default int writeFixed(double[] data) throws IOException {
        int written = 0;
        for (double d : data) {
            written += write(d);
        }
        return written;
    }
    default int writeVariable(double[] data) throws IOException {
        return write(data.length) + writeFixed(data);
    }
    default int writeFixed(Collection<? extends XdrSerializable> data) throws IOException {
        int written = 0;
        for (XdrSerializable item : data) {
            written += write(item);
        }
        return written;
    }
    default int writeVariable(Collection<? extends XdrSerializable> data) throws IOException {
        return write(data.size()) + writeFixed(data);
    }
}
