package com.github.lucbui.bytes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteUtilsTest {

    @Test
    void assertRangeRunsCorrectly() {
        assertDoesNotThrow(() -> ByteUtils.assertRange(2, 1, 3));
    }

    @Test
    void assertRangeBelowLowest() {
        assertThrows(IllegalArgumentException.class, () -> ByteUtils.assertRange(0, 1, 3));
    }

    @Test
    void assertRangeAboveHighest() {
        assertThrows(IllegalArgumentException.class, () -> ByteUtils.assertRange(5, 1, 3));
    }

    @Test
    void assertRangeLowestHighestSwapped() {
        assertThrows(IllegalArgumentException.class, () -> ByteUtils.assertRange(0, 3, 1));
    }

    @Test
    void assertByteToUnsignedByteIdentity() {
        assertEquals(0, ByteUtils.byteToUnsignedByte((byte)0));
    }

    @Test
    void assertByteToUnsignedByteNonIdentity() {
        assertEquals(255, ByteUtils.byteToUnsignedByte((byte)-1));
    }
}