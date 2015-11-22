package net.radai.bob.runtime.wire;

import java.io.IOException;

/**
 * @author Radai Rosenblatt
 */
public interface XdrInput {
    boolean readBoolean() throws IOException;
}
