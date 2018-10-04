package com.github.lucbui.bytes;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class HexUtilsTest {

    @Test
    void assertRangeRunsCorrectly() {
        assertDoesNotThrow(() -> HexUtils.assertRange(2, 1, 3));
    }

    @Test
    void assertRangeBelowLowest() {
        assertThrows(IllegalArgumentException.class, () -> HexUtils.assertRange(0, 1, 3));
    }

    @Test
    void assertRangeAboveHighest() {
        assertThrows(IllegalArgumentException.class, () -> HexUtils.assertRange(5, 1, 3));
    }

    @Test
    void assertRangeLowestHighestSwapped() {
        assertThrows(IllegalArgumentException.class, () -> HexUtils.assertRange(0, 3, 1));
    }

    @Test
    void assertByteToUnsignedByteIdentity() {
        assertEquals(0, HexUtils.byteToUnsignedByte((byte)0));
    }

    @Test
    void assertUnsignedByteToByteIdentity() {
        assertEquals(0, HexUtils.unsignedByteToByte(0));
    }

    @Test
    void assertByteToUnsignedByteNonIdentity() {
        assertEquals(255, HexUtils.byteToUnsignedByte((byte)-1));
    }

    @Test
    void assertUnsignedByteToByteNonIdentity(){
        assertEquals(-1, HexUtils.unsignedByteToByte(255));
    }

    @Test
    void assertToByteBufferNone(){
        assertEquals(0, HexUtils.toByteBuffer().capacity());
    }

    @Test
    void assertToByteBufferOne(){
        assertEquals(1, HexUtils.toByteBuffer(0).capacity());
    }

    @Test
    void assertToByteBufferUnsigned(){
        assertEquals(HexUtils.toByteBuffer(8), ByteBuffer.wrap(new byte[]{(byte)8}));
    }

    @Test
    void assertToByteBufferSigned(){
        assertEquals(HexUtils.toByteBuffer(255), ByteBuffer.wrap(new byte[]{(byte)-1}));
    }
}