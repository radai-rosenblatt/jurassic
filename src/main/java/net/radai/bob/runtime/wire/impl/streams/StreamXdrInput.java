package net.radai.bob.runtime.wire.impl.streams;

import net.radai.bob.runtime.wire.XdrInput;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by Radai Rosenblatt
 */
public class StreamXdrInput implements XdrInput, Closeable {
    private final Charset ascii = Charset.forName("ASCII");
    private final InputStream in;

    public StreamXdrInput(InputStream in) {
        this.in = in;
    }

    private final byte[] buf = new byte[8];

    @Override
    public int readInt() throws IOException {
        readIntoBuffer(4, buf);
        return (
                ((buf[0] & 0xff) << 24)
              | ((buf[1] & 0xff) << 16)
              | ((buf[2] & 0xff) <<  8)
              | (buf[3] & 0xff)
        );
    }

    @Override
    public long readLong() throws IOException {
        readIntoBuffer(8, buf);
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
    public String readString() throws IOException {
        int numChars = readInt();
        byte[] buffer = new byte[numChars];
        readIntoBuffer(numChars, buffer);
        return ascii.decode(ByteBuffer.wrap(buffer)).toString();
    }



    @Override
    public byte[] readFixedByteArray(int ofSize) throws IOException {
        if (ofSize == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[ofSize];
        readIntoBuffer(ofSize, result);
        int padding = ofSize % 4;
        if (padding != 0) {
            padding = 4-padding;
            readIntoBuffer(padding, buf);
        }
        return result;
    }

    @Override
    public void close() throws IOException {

    }

    private void readIntoBuffer(int numChars, byte[] buffer) throws IOException {
        int bytesRead = in.read(buffer, 0, numChars);
        if (bytesRead != numChars) {
            throw new IllegalStateException("expected to read " + numChars + " bytes. instead got " + bytesRead);
        }
    }
}
