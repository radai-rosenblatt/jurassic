/*
 * This file is part of Bob.
 *
 * Bob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Bob. If not, see <http://www.gnu.org/licenses/>.
 */

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
