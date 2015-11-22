package net.radai.bob.runtime.model;

import net.radai.bob.runtime.wire.XdrOutput;

import java.io.IOException;

/**
 * @author Radai Rosenblatt
 */
public interface XdrEnum extends XdrSerializable {
    int value();

    @Override
    default void serializeTo(XdrOutput output) throws IOException {
        output.write(value());
    }
}
