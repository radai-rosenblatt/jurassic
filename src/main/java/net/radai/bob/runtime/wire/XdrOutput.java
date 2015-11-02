package net.radai.bob.runtime.wire;

import net.radai.bob.runtime.model.XdrSerializable;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Radai Rosenblatt
 */
public interface XdrOutput {
    void write(boolean data) throws IOException;
    void write(int data) throws IOException;
    void write(long data) throws IOException;
    void write(float data) throws IOException;
    void write(double data) throws IOException;
    void write(XdrSerializable data) throws IOException;
    void writeFixed(byte[] data) throws IOException;
    void writeVariable(byte[] data) throws IOException;
    //TODO - streams, byte-buffers, channels dtc
    void write(String data) throws IOException; //ascii
    void writeFixed(Collection<? extends XdrSerializable> data) throws IOException;
    void writeVariable(Collection<? extends XdrSerializable> data) throws IOException;
}
