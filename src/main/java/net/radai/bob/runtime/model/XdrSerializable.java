package net.radai.bob.runtime.model;

import net.radai.bob.runtime.wire.XdrOutput;

import java.io.IOException;

/**
 * @author Radai Rosenblatt
 */
public interface XdrSerializable {
    /**
     * @param output XdrOutput to serialize to
     * @return number of bytes written to output
     * @throws IOException
     */
    int serializeTo(XdrOutput output) throws IOException;
}
