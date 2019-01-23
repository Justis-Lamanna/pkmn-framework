package com.github.lucbui.bytes;

import com.github.lucbui.utility.HexUtils;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class HexUtilsTest {

    @Test
    public void assertRangeRunsCorrectly() {
        HexUtils.assertRange(2, 1, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertRangeBelowLowest() {
        HexUtils.assertRange(0, 1, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertRangeAboveHighest() {
        HexUtils.assertRange(5, 1, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertRangeLowestHighestSwapped() {
        HexUtils.assertRange(0, 3, 1);
    }

    @Test
    public void assertByteToUnsignedByteIdentity() {
        assertEquals(0, HexUtils.byteToUnsignedByte((byte)0));
    }

    @Test
    public void assertUnsignedByteToByteIdentity() {
        assertEquals(0, HexUtils.unsignedByteToByte(0));
    }

    @Test
    public void assertByteToUnsignedByteNonIdentity() {
        assertEquals(255, HexUtils.byteToUnsignedByte((byte)-1));
    }

    @Test
    public void assertUnsignedByteToByteNonIdentity(){
        assertEquals(-1, HexUtils.unsignedByteToByte(255));
    }

    @Test
    public void assertToByteBufferNone(){
        assertEquals(0, HexUtils.toByteBuffer().capacity());
    }

    @Test
    public void assertToByteBufferOne(){
        assertEquals(1, HexUtils.toByteBuffer(0).capacity());
    }

    @Test
    public void assertToByteBufferUnsigned(){
        assertEquals(HexUtils.toByteBuffer(8), ByteBuffer.wrap(new byte[]{(byte)8}));
    }

    @Test
    public void assertToByteBufferSigned(){
        assertEquals(HexUtils.toByteBuffer(255), ByteBuffer.wrap(new byte[]{(byte)-1}));
    }
}