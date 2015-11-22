package net.radai.bob.runtime.wire.impl.streams;

import net.radai.bob.runtime.wire.XdrInput;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Radai Rosenblatt
 */
public class StreamXdrInput implements XdrInput, Closeable {
    private InputStream in;

    public StreamXdrInput(InputStream in) {
        this.in = in;
    }

    private final byte[] buf = new byte[8];

    @Override
    public boolean readBoolean() throws IOException {
        read4Bytes();
        return buf[3] != 0; //very lax
    }

    @Override
    public int readInt() throws IOException {
        read4Bytes();
        return (
                ((buf[0] & 0xff) << 24)
              | ((buf[1] & 0xff) << 16)
              | ((buf[2] & 0xff) <<  8)
              | (buf[3] & 0xff)
        );
    }

    @Override
    public long readLong() throws IOException {
        read8Bytes();
        return (
                (((long)buf[0] & 0xff) << 56)
              | (((long)buf[1] & 0xff) << 48)
              | (((long)buf[2] & 0xff) << 40)
              | (((long)buf[3] & 0xff) << 32)
              | (((long)buf[4] & 0xff) << 24)
              | (((long)buf[5] & 0xff) << 16)
              | (((long)buf[6] & 0xff) <<  8)
              | (((long)buf[7] & 0xff))
        );
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    //TODO - optimize these to use int read() directly
    private void read4Bytes() throws IOException {
        int bytesRead = in.read(buf, 0, 4);
        if (bytesRead != 4) {
            throw new IllegalStateException("expected to read 4 bytes. instead got " + bytesRead);
        }
    }

    private void read8Bytes() throws IOException {
        int bytesRead = in.read(buf, 0, 8);
        if (bytesRead != 8) {
            throw new IllegalStateException("expected to read 8 bytes. instead got " + bytesRead);
        }
    }

    @Override
    public void close() throws IOException {

    }
}
