package net.radai.bob.runtime.model;

import net.radai.bob.runtime.wire.XdrOutput;

import java.io.IOException;

/**
 * @author Radai Rosenblatt
 */
public interface XdrSerializable {
    /**
     * must produce padding as well - pad with 0's to a multiple of 4 bytes
     * @param output XdrOutput to serialize to
     * @throws IOException
     */
    void serializeTo(XdrOutput output) throws IOException;
}
