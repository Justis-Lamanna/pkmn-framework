package com.github.lucbui.bytes;

import org.junit.jupiter.api.Test;

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
    void assertByteToUnsignedByteNonIdentity() {
        assertEquals(255, HexUtils.byteToUnsignedByte((byte)-1));
    }
}