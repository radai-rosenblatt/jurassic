package net.radai.bob.runtime.model;

import net.radai.bob.runtime.wire.XdrOutput;

import java.io.IOException;

/**
 * @author Radai Rosenblatt
 */
public interface XdrEnum extends XdrSerializable {
    int value();

    @Override
    default int serializeTo(XdrOutput output) throws IOException {
        return output.write(value());
    }
}
