package net.radai.bob.runtime.wire.impl.streams;

import net.radai.bob.runtime.model.XdrSerializable;
import net.radai.bob.runtime.wire.XdrOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * @author Radai Rosenblatt
 */
public class StreamXdrOutput implements XdrOutput {
    private OutputStream out;

    public StreamXdrOutput(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(boolean data) throws IOException{
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(data ? 1 : 0);
    }

    @Override
    public void write(int data) throws IOException{
        out.write((data >>> 24) & 0xFF);
        out.write((data >>> 16) & 0xFF);
        out.write((data >>>  8) & 0xFF);
        out.write((data) & 0xFF);
    }

    private byte writeBuffer[] = new byte[8];

    @Override
    public void write(long data) throws IOException{
        writeBuffer[0] = (byte)(data >>> 56);
        writeBuffer[1] = (byte)(data >>> 48);
        writeBuffer[2] = (byte)(data >>> 40);
        writeBuffer[3] = (byte)(data >>> 32);
        writeBuffer[4] = (byte)(data >>> 24);
        writeBuffer[5] = (byte)(data >>> 16);
        writeBuffer[6] = (byte)(data >>>  8);
        writeBuffer[7] = (byte)(data);
        out.write(writeBuffer, 0, 8);
    }

    @Override
    public void write(float data) throws IOException{
        write(Float.floatToIntBits(data));
    }

    @Override
    public void write(double data) throws IOException{
        write(Double.doubleToLongBits(data));
    }

    @Override
    public void write(XdrSerializable data) throws IOException{
        data.serializeTo(this);
    }

    @Override
    public void writeFixed(byte[] data) throws IOException{
        out.write(data);
        switch (data.length % 4) {
            case 3:
                out.write(0);
            case 2:
                out.write(0);
            case 1:
                out.write(0);
            case 0:
                break;
        }
    }

    @Override
    public void writeVariable(byte[] data) throws IOException{
        write(data.length);
        writeFixed(data);
    }

    private final static Charset ASCII = Charset.forName("ascii");

    @Override
    public void write(String data) throws IOException{
        ByteBuffer asciiEncoded = ASCII.encode(data);
        writeVariable(asciiEncoded.array());
    }

    @Override
    public void writeFixed(Collection<? extends XdrSerializable> data) throws IOException{
        for (XdrSerializable datum : data) {
            datum.serializeTo(this);
        }
    }

    @Override
    public void writeVariable(Collection<? extends XdrSerializable> data) throws IOException{
        write(data.size());
        writeFixed(data);
    }
}
