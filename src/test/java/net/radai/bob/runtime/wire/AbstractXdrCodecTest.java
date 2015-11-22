package net.radai.bob.runtime.wire;

import org.dcache.xdr.Xdr;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.HeapBuffer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Radai Rosenblatt
 */
public abstract class AbstractXdrCodecTest {

    protected abstract XdrOutput buildOutput();
    protected abstract byte[] getPayload(XdrOutput from);
    protected abstract XdrInput buildInput(byte[] from);

    protected Xdr buildOncrpc4j() {
        return new Xdr(1024);
    }

    protected Xdr buildOncrpc4j(byte[] from) {
        Buffer buffer = HeapBuffer.wrap(from);
        return new Xdr(buffer);
    }

    protected byte[] getPayload(Xdr oncrpc4j) {
        oncrpc4j.endEncoding();
        Buffer buffer = oncrpc4j.asBuffer();
        byte[] payload = new byte[buffer.limit()];
        buffer.get(payload);
        return payload;
    }

    protected XdrInput flip(XdrOutput output) {
        byte[] payload = getPayload(output);
        return buildInput(payload);
    }

    @Test
    public void testRoundTripBoolean() throws Exception {
        for (boolean boolValue : new boolean[] {false, true}) {
            XdrOutput output = buildOutput();
            output.write(boolValue);
            XdrInput input = flip(output);
            boolean read = input.readBoolean();
            Assert.assertEquals(boolValue, read);
        }
    }

    @Test
    public void testCompatibilityBoolean() throws Exception {
        for (boolean boolValue : new boolean[] {false, true}) {
            //oncrpc4j --> bob
            Xdr oncrpc4j = buildOncrpc4j();
            oncrpc4j.xdrEncodeBoolean(boolValue);
            byte[] payload = getPayload(oncrpc4j);
            XdrInput input = buildInput(payload);
            boolean read = input.readBoolean();
            Assert.assertEquals(boolValue, read);

            //bob --> oncrpc4j
            XdrOutput output = buildOutput();
            output.write(boolValue);
            byte[] payload2 = getPayload(output);
            Assert.assertArrayEquals(payload, payload2); //binary compatibility
            oncrpc4j = buildOncrpc4j(payload2);
            read = oncrpc4j.xdrDecodeBoolean();
            Assert.assertEquals(boolValue, read);
        }
    }
}
