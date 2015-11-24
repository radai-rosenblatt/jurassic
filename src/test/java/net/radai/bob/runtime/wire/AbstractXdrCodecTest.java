package net.radai.bob.runtime.wire;

import org.dcache.xdr.Xdr;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.HeapBuffer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

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
        for (boolean value : new boolean[] {false, true}) {
            XdrOutput output = buildOutput();
            output.write(value);
            XdrInput input = flip(output);
            boolean read = input.readBoolean();
            Assert.assertEquals(value, read);
        }
    }

    @Test
    public void testCompatibilityBoolean() throws Exception {
        for (boolean value : new boolean[] {false, true}) {
            //oncrpc4j --> bob
            Xdr oncrpc4j = buildOncrpc4j();
            oncrpc4j.xdrEncodeBoolean(value);
            byte[] payload = getPayload(oncrpc4j);
            XdrInput input = buildInput(payload);
            boolean read = input.readBoolean();
            Assert.assertEquals(value, read);

            //bob --> oncrpc4j
            XdrOutput output = buildOutput();
            int written = output.write(value);
            byte[] payload2 = getPayload(output);
            Assert.assertEquals(written, payload2.length);
            Assert.assertArrayEquals(payload, payload2); //binary compatibility
            oncrpc4j = buildOncrpc4j(payload2);
            read = oncrpc4j.xdrDecodeBoolean();
            Assert.assertEquals(value, read);
        }
    }

    @Test
    public void testRoundTripInteger() throws Exception {
        for (int value : new int[] {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE}) {
            XdrOutput output = buildOutput();
            output.write(value);
            XdrInput input = flip(output);
            int read = input.readInt();
            Assert.assertEquals(value, read);
        }
    }

    @Test
    public void testCompatibilityInteger() throws Exception {
        for (int value : new int[] {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE}) {
            //oncrpc4j --> bob
            Xdr oncrpc4j = buildOncrpc4j();
            oncrpc4j.xdrEncodeInt(value);
            byte[] payload = getPayload(oncrpc4j);
            XdrInput input = buildInput(payload);
            int read = input.readInt();
            Assert.assertEquals(value, read);

            //bob --> oncrpc4j
            XdrOutput output = buildOutput();
            int written = output.write(value);
            byte[] payload2 = getPayload(output);
            Assert.assertEquals(written, payload2.length);
            Assert.assertArrayEquals(payload, payload2); //binary compatibility
            oncrpc4j = buildOncrpc4j(payload2);
            read = oncrpc4j.xdrDecodeInt();
            Assert.assertEquals(value, read);
        }
    }

    @Test
    public void testRoundTripLong() throws Exception {
        for (long value : new long[] {
                Long.MIN_VALUE, Integer.MIN_VALUE - 1L, Integer.MIN_VALUE, -1, 0,
                1, Integer.MAX_VALUE, Integer.MAX_VALUE+1L, Long.MAX_VALUE}) {
            XdrOutput output = buildOutput();
            output.write(value);
            XdrInput input = flip(output);
            long read = input.readLong();
            Assert.assertEquals(value, read);
        }
    }

    @Test
    public void testCompatibilityLong() throws Exception {
        for (long value : new long[] {
                Long.MIN_VALUE, Integer.MIN_VALUE - 1L, Integer.MIN_VALUE, -1, 0,
                1, Integer.MAX_VALUE, Integer.MAX_VALUE+1L, Long.MAX_VALUE}) {
            //oncrpc4j --> bob
            Xdr oncrpc4j = buildOncrpc4j();
            oncrpc4j.xdrEncodeLong(value);
            byte[] payload = getPayload(oncrpc4j);
            XdrInput input = buildInput(payload);
            long read = input.readLong();
            Assert.assertEquals(value, read);

            //bob --> oncrpc4j
            XdrOutput output = buildOutput();
            int written = output.write(value);
            byte[] payload2 = getPayload(output);
            Assert.assertEquals(written, payload2.length);
            Assert.assertArrayEquals(payload, payload2); //binary compatibility
            oncrpc4j = buildOncrpc4j(payload2);
            read = oncrpc4j.xdrDecodeLong();
            Assert.assertEquals(value, read);
        }
    }

    @Test
    public void testRoundTripFloat() throws Exception {
        for (float value : new float[] {-Float.MAX_VALUE, -Float.MIN_NORMAL,
                -Float.MIN_VALUE, 0, Float.MIN_VALUE, Float.MIN_NORMAL, Float.MAX_VALUE}) {
            XdrOutput output = buildOutput();
            output.write(value);
            XdrInput input = flip(output);
            float read = input.readFloat();
            Assert.assertEquals(value, read, 0);
        }
    }

    @Test
    public void testCompatibilityFloat() throws Exception {
        for (float value : new float[] {-Float.MAX_VALUE, -Float.MIN_NORMAL,
                -Float.MIN_VALUE, 0, Float.MIN_VALUE, Float.MIN_NORMAL, Float.MAX_VALUE}) {
            //oncrpc4j --> bob
            Xdr oncrpc4j = buildOncrpc4j();
            oncrpc4j.xdrEncodeFloat(value);
            byte[] payload = getPayload(oncrpc4j);
            XdrInput input = buildInput(payload);
            float read = input.readFloat();
            Assert.assertEquals(value, read, 0);

            //bob --> oncrpc4j
            XdrOutput output = buildOutput();
            int written = output.write(value);
            byte[] payload2 = getPayload(output);
            Assert.assertEquals(written, payload2.length);
            Assert.assertArrayEquals(payload, payload2); //binary compatibility
            oncrpc4j = buildOncrpc4j(payload2);
            read = oncrpc4j.xdrDecodeFloat();
            Assert.assertEquals(value, read, 0);
        }
    }

    @Test
    public void testRoundTripDouble() throws Exception {
        for (double value : new double[] {-Double.MAX_VALUE, -Float.MAX_VALUE, -Float.MIN_NORMAL,
                -Float.MIN_VALUE, 0, Float.MIN_VALUE, Float.MIN_NORMAL, Float.MAX_VALUE, Double.MAX_VALUE}) {
            XdrOutput output = buildOutput();
            output.write(value);
            XdrInput input = flip(output);
            double read = input.readDouble();
            Assert.assertEquals(value, read, 0);
        }
    }

    @Test
    public void testCompatibilityDouble() throws Exception {
        for (double value : new double[] {-Double.MAX_VALUE, -Float.MAX_VALUE, -Float.MIN_NORMAL,
                -Float.MIN_VALUE, 0, Float.MIN_VALUE, Float.MIN_NORMAL, Float.MAX_VALUE, Double.MAX_VALUE}) {
            //oncrpc4j --> bob
            Xdr oncrpc4j = buildOncrpc4j();
            oncrpc4j.xdrEncodeDouble(value);
            byte[] payload = getPayload(oncrpc4j);
            XdrInput input = buildInput(payload);
            double read = input.readDouble();
            Assert.assertEquals(value, read, 0);

            //bob --> oncrpc4j
            XdrOutput output = buildOutput();
            int written = output.write(value);
            byte[] payload2 = getPayload(output);
            Assert.assertEquals(written, payload2.length);
            Assert.assertArrayEquals(payload, payload2); //binary compatibility
            oncrpc4j = buildOncrpc4j(payload2);
            read = oncrpc4j.xdrDecodeDouble();
            Assert.assertEquals(value, read, 0);
        }
    }

    @Test
    public void testRoundTripString() throws Exception {
        for (String value : new String[] {"a", "a b", "a\n\n\tb", "\n\r\t", "   "}) {
            XdrOutput output = buildOutput();
            output.write(value);
            XdrInput input = flip(output);
            String read = input.readString();
            Assert.assertEquals(value, read);
        }
    }

    @Test
    public void testCompatibilityString() throws Exception {
        for (String value : new String[] {"a", "a b", "a\n\n\tb", "\n\r\t", "   "}) {
            //oncrpc4j --> bob
            Xdr oncrpc4j = buildOncrpc4j();
            oncrpc4j.xdrEncodeString(value);
            byte[] payload = getPayload(oncrpc4j);
            XdrInput input = buildInput(payload);
            String read = input.readString();
            Assert.assertEquals(value, read);

            //bob --> oncrpc4j
            XdrOutput output = buildOutput();
            int written = output.write(value);
            byte[] payload2 = getPayload(output);
            Assert.assertEquals(written, payload2.length);
            Assert.assertArrayEquals(payload, payload2); //binary compatibility
            oncrpc4j = buildOncrpc4j(payload2);
            read = oncrpc4j.xdrDecodeString();
            Assert.assertEquals(value, read);
        }
    }

    @Test
    public void testRoundTripFixedByteArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                byte[] value = new byte[r.nextInt(100)];
                r.nextBytes(value);

                XdrOutput output = buildOutput();
                output.writeFixed(value);
                XdrInput input = flip(output);
                byte[] read = input.readFixedByteArray(value.length);
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testCompatibilityFixedByteArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                byte[] value = new byte[r.nextInt(100)];
                r.nextBytes(value);

                //oncrpc4j --> bob
                Xdr oncrpc4j = buildOncrpc4j();
                oncrpc4j.xdrEncodeOpaque(value, value.length);
                byte[] payload = getPayload(oncrpc4j);
                XdrInput input = buildInput(payload);
                byte[] read = input.readFixedByteArray(value.length);
                Assert.assertArrayEquals(value, read);

                //bob --> oncrpc4j
                XdrOutput output = buildOutput();
                int written = output.writeFixed(value);
                byte[] payload2 = getPayload(output);
                Assert.assertEquals(written, payload2.length);
                Assert.assertArrayEquals(payload, payload2); //binary compatibility
                oncrpc4j = buildOncrpc4j(payload2);
                read = oncrpc4j.xdrDecodeOpaque(value.length);
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testRoundTripVariableByteArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                byte[] value = new byte[r.nextInt(100)];
                r.nextBytes(value);

                XdrOutput output = buildOutput();
                output.writeVariable(value);
                XdrInput input = flip(output);
                byte[] read = input.readVariableByteArray();
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testCompatibilityVariableByteArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                byte[] value = new byte[r.nextInt(100)];
                r.nextBytes(value);

                //oncrpc4j --> bob
                Xdr oncrpc4j = buildOncrpc4j();
                oncrpc4j.xdrEncodeDynamicOpaque(value);
                byte[] payload = getPayload(oncrpc4j);
                XdrInput input = buildInput(payload);
                byte[] read = input.readVariableByteArray();
                Assert.assertArrayEquals(value, read);

                //bob --> oncrpc4j
                XdrOutput output = buildOutput();
                int written = output.writeVariable(value);
                byte[] payload2 = getPayload(output);
                Assert.assertEquals(written, payload2.length);
                Assert.assertArrayEquals(payload, payload2); //binary compatibility
                oncrpc4j = buildOncrpc4j(payload2);
                read = oncrpc4j.xdrDecodeDynamicOpaque();
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testRoundTripFixedBooleanArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                boolean[] value = new boolean[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextBoolean();
                }

                XdrOutput output = buildOutput();
                output.writeFixed(value);
                XdrInput input = flip(output);
                boolean[] read = input.readFixedBooleanArray(value.length);
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    //oncrpc doesnt support fixed bool[] ?

    @Test
    public void testRoundTripVariableBooleanArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                boolean[] value = new boolean[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextBoolean();
                }

                XdrOutput output = buildOutput();
                output.writeVariable(value);
                XdrInput input = flip(output);
                boolean[] read = input.readVariableBooleanArray();
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    //oncrpc doesnt support variable bool[] ?

    @Test
    public void testRoundTripFixedIntegerArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                int[] value = new int[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextInt();
                }

                XdrOutput output = buildOutput();
                output.writeFixed(value);
                XdrInput input = flip(output);
                int[] read = input.readFixedIntArray(value.length);
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    //oncrpc doesnt support fixed int[] ?

    @Test
    public void testRoundTripVariableIntegerArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                int[] value = new int[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextInt();
                }

                XdrOutput output = buildOutput();
                output.writeVariable(value);
                XdrInput input = flip(output);
                int[] read = input.readVariableIntArray();
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testCompatibilityVariableIntegerArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                int[] value = new int[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextInt();
                }

                //oncrpc4j --> bob
                Xdr oncrpc4j = buildOncrpc4j();
                oncrpc4j.xdrEncodeIntVector(value);
                byte[] payload = getPayload(oncrpc4j);
                XdrInput input = buildInput(payload);
                int[] read = input.readVariableIntArray();
                Assert.assertArrayEquals(value, read);

                //bob --> oncrpc4j
                XdrOutput output = buildOutput();
                int written = output.writeVariable(value);
                byte[] payload2 = getPayload(output);
                Assert.assertEquals(written, payload2.length);
                Assert.assertArrayEquals(payload, payload2); //binary compatibility
                oncrpc4j = buildOncrpc4j(payload2);
                read = oncrpc4j.xdrDecodeIntVector();
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testRoundTripFixedLongArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                long[] value = new long[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextLong();
                }

                XdrOutput output = buildOutput();
                output.writeFixed(value);
                XdrInput input = flip(output);
                long[] read = input.readFixedLongArray(value.length);
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    //oncrpc doesnt support fixed long[] ?

    @Test
    public void testRoundTripVariableLongArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                long[] value = new long[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextLong();
                }

                XdrOutput output = buildOutput();
                output.writeVariable(value);
                XdrInput input = flip(output);
                long[] read = input.readVariableLongArray();
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testCompatibilityVariableLongArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                long[] value = new long[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextLong();
                }

                //oncrpc4j --> bob
                Xdr oncrpc4j = buildOncrpc4j();
                oncrpc4j.xdrEncodeLongVector(value);
                byte[] payload = getPayload(oncrpc4j);
                XdrInput input = buildInput(payload);
                long[] read = input.readVariableLongArray();
                Assert.assertArrayEquals(value, read);

                //bob --> oncrpc4j
                XdrOutput output = buildOutput();
                int written = output.writeVariable(value);
                byte[] payload2 = getPayload(output);
                Assert.assertEquals(written, payload2.length);
                Assert.assertArrayEquals(payload, payload2); //binary compatibility
                oncrpc4j = buildOncrpc4j(payload2);
                read = oncrpc4j.xdrDecodeLongVector();
                Assert.assertArrayEquals(value, read);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testRoundTripFixedFloatArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                float[] value = new float[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextFloat();
                }

                XdrOutput output = buildOutput();
                output.writeFixed(value);
                XdrInput input = flip(output);
                float[] read = input.readFixedFloatArray(value.length);
                Assert.assertArrayEquals(value, read, 0);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testCompatibilityFixedFloatArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                float[] value = new float[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextFloat();
                }

                //oncrpc4j --> bob
                Xdr oncrpc4j = buildOncrpc4j();
                oncrpc4j.xdrEncodeFloatFixedVector(value, value.length);
                byte[] payload = getPayload(oncrpc4j);
                XdrInput input = buildInput(payload);
                float[] read = input.readFixedFloatArray(value.length);
                Assert.assertArrayEquals(value, read, 0);

                //bob --> oncrpc4j
                XdrOutput output = buildOutput();
                int written = output.writeFixed(value);
                byte[] payload2 = getPayload(output);
                Assert.assertEquals(written, payload2.length);
                Assert.assertArrayEquals(payload, payload2); //binary compatibility
                oncrpc4j = buildOncrpc4j(payload2);
                read = oncrpc4j.xdrDecodeFloatFixedVector(value.length);
                Assert.assertArrayEquals(value, read, 0);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testRoundTripVariableFloatArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                float[] value = new float[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextFloat();
                }

                XdrOutput output = buildOutput();
                output.writeVariable(value);
                XdrInput input = flip(output);
                float[] read = input.readVariableFloatArray();
                Assert.assertArrayEquals(value, read, 0);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testCompatibilityVariableFloatArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                float[] value = new float[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextFloat();
                }

                //oncrpc4j --> bob
                Xdr oncrpc4j = buildOncrpc4j();
                oncrpc4j.xdrEncodeFloatVector(value);
                byte[] payload = getPayload(oncrpc4j);
                XdrInput input = buildInput(payload);
                float[] read = input.readVariableFloatArray();
                Assert.assertArrayEquals(value, read, 0);

                //bob --> oncrpc4j
                XdrOutput output = buildOutput();
                int written = output.writeVariable(value);
                byte[] payload2 = getPayload(output);
                Assert.assertEquals(written, payload2.length);
                Assert.assertArrayEquals(payload, payload2); //binary compatibility
                oncrpc4j = buildOncrpc4j(payload2);
                read = oncrpc4j.xdrDecodeFloatVector();
                Assert.assertArrayEquals(value, read, 0);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testRoundTripFixedDoubleArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                double[] value = new double[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextDouble();
                }

                XdrOutput output = buildOutput();
                output.writeFixed(value);
                XdrInput input = flip(output);
                double[] read = input.readFixedDoubleArray(value.length);
                Assert.assertArrayEquals(value, read, 0);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testCompatibilityFixedDoubleArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                double[] value = new double[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextDouble();
                }

                //oncrpc4j --> bob
                Xdr oncrpc4j = buildOncrpc4j();
                oncrpc4j.xdrEncodeDoubleFixedVector(value, value.length);
                byte[] payload = getPayload(oncrpc4j);
                XdrInput input = buildInput(payload);
                double[] read = input.readFixedDoubleArray(value.length);
                Assert.assertArrayEquals(value, read, 0);

                //bob --> oncrpc4j
                XdrOutput output = buildOutput();
                int written = output.writeFixed(value);
                byte[] payload2 = getPayload(output);
                Assert.assertEquals(written, payload2.length);
                Assert.assertArrayEquals(payload, payload2); //binary compatibility
                oncrpc4j = buildOncrpc4j(payload2);
                read = oncrpc4j.xdrDecodeDoubleFixedVector(value.length);
                Assert.assertArrayEquals(value, read, 0);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testRoundTripVariableDoubleArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                double[] value = new double[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextDouble();
                }

                XdrOutput output = buildOutput();
                output.writeVariable(value);
                XdrInput input = flip(output);
                double[] read = input.readVariableDoubleArray();
                Assert.assertArrayEquals(value, read, 0);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }

    @Test
    public void testCompatibilityVariableDoubleArray() throws Exception {
        long seed = 42;
        for (int i=0; i<10; i++) {
            try {
                seed = System.currentTimeMillis();
                Random r = new Random(seed);
                double[] value = new double[r.nextInt(100)];
                for (int j=0; j<value.length; j++) {
                    value[j] = r.nextDouble();
                }

                //oncrpc4j --> bob
                Xdr oncrpc4j = buildOncrpc4j();
                oncrpc4j.xdrEncodeDoubleVector(value);
                byte[] payload = getPayload(oncrpc4j);
                XdrInput input = buildInput(payload);
                double[] read = input.readVariableDoubleArray();
                Assert.assertArrayEquals(value, read, 0);

                //bob --> oncrpc4j
                XdrOutput output = buildOutput();
                int written = output.writeVariable(value);
                byte[] payload2 = getPayload(output);
                Assert.assertEquals(written, payload2.length);
                Assert.assertArrayEquals(payload, payload2); //binary compatibility
                oncrpc4j = buildOncrpc4j(payload2);
                read = oncrpc4j.xdrDecodeDoubleVector();
                Assert.assertArrayEquals(value, read, 0);
            } catch (Exception | AssertionError e) {
                System.err.println("seed is " + seed);
                throw e;
            }
        }
    }
}
