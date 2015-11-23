package net.radai.bob.runtime.wire.impl.streams;

import net.radai.bob.runtime.wire.XdrOutput;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Radai Rosenblatt
 */
public class StreamXdrOutput implements XdrOutput, Closeable {
    private OutputStream out;

    public StreamXdrOutput(OutputStream out) {
        this.out = out;
    }

    private byte writeBuffer[] = new byte[8];

    @Override
    public int padZeroes(int numBytes) throws IOException {
        switch (numBytes) {
            case 3:
                out.write(0);
            case 2:
                out.write(0);
            case 1:
                out.write(0);
            case 0:
                break;
            default:
                throw new IllegalArgumentException("" + numBytes);
        }
        return numBytes;
    }

    @Override
    public int write(int data) throws IOException{
        out.write((data >>> 24) & 0xFF);
        out.write((data >>> 16) & 0xFF);
        out.write((data >>>  8) & 0xFF);
        out.write((data) & 0xFF);
        return 4;
    }

    @Override
    public int write(long data) throws IOException{
        writeBuffer[0] = (byte)(data >>> 56);
        writeBuffer[1] = (byte)(data >>> 48);
        writeBuffer[2] = (byte)(data >>> 40);
        writeBuffer[3] = (byte)(data >>> 32);
        writeBuffer[4] = (byte)(data >>> 24);
        writeBuffer[5] = (byte)(data >>> 16);
        writeBuffer[6] = (byte)(data >>>  8);
        writeBuffer[7] = (byte)(data);
        out.write(writeBuffer, 0, 8);
        return 8;
    }

    @Override
    public int writeFixed(byte[] data) throws IOException{
        out.write(data);
        int written = data.length;
        int padding = written % 4;
        if (padding == 0) {
            return written;
        }
        return written + padZeroes(4 - padding);
    }

    @Override
    public void close() throws IOException {
        //nop. we don't close the underlying stream
    }
}