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

    private final byte[] FOUR_BYTES = new byte[4];

    @Override
    public boolean readBoolean() throws IOException {
        readFourBytes();
        return FOUR_BYTES[3] != 0; //very lax
    }

    private void readFourBytes() throws IOException {
        int bytesRead = in.read(FOUR_BYTES);
        if (bytesRead != 4) {
            throw new IllegalStateException("expected to read 4 bytes. instead got " + bytesRead);
        }
    }

    @Override
    public void close() throws IOException {

    }
}
