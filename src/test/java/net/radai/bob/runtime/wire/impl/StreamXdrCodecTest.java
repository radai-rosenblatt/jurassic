package net.radai.bob.runtime.wire.impl;

import net.radai.bob.runtime.wire.AbstractXdrCodecTest;
import net.radai.bob.runtime.wire.XdrInput;
import net.radai.bob.runtime.wire.XdrOutput;
import net.radai.bob.runtime.wire.impl.streams.StreamXdrInput;
import net.radai.bob.runtime.wire.impl.streams.StreamXdrOutput;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by Radai Rosenblatt
 */
public class StreamXdrCodecTest extends AbstractXdrCodecTest {

    @Override
    protected XdrOutput buildOutput() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        return new StreamXdrOutput(bos);
    }

    @Override
    protected byte[] getPayload(XdrOutput from) {
        StreamXdrOutput streamOut = (StreamXdrOutput) from;
        OutputStream out = (OutputStream) ReflectionTestUtils.getField(streamOut, "out");
        ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
        return bos.toByteArray();
    }

    @Override
    protected XdrInput buildInput(byte[] from) {
        ByteArrayInputStream bis = new ByteArrayInputStream(from);
        return new StreamXdrInput(bis);
    }
}
