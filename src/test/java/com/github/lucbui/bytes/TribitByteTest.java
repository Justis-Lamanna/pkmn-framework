package com.github.lucbui.bytes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TribitByteTest {
    @Test
    public void testEqualsDontCare() {
        TribitByte biteDontCare = TribitByte.value(0, 0, 0, 0, 1, 1, 1, null);
        TribitByte bite1 = TribitByte.value(0, 0, 0, 0, 1, 1, 1, 1);
        TribitByte bite0 = TribitByte.value(0, 0, 0, 0, 1, 1, 1, 0);

        assertTrue(biteDontCare.equalsDontCare(bite1));
        assertTrue(biteDontCare.equalsDontCare(bite0));
    }

    @Test
    public void testClone() {
        TribitByte biteDontCare = TribitByte.value(0, 0, 0, 0, 1, 1, 1, null);
        TribitByte clone = TribitByte.value(biteDontCare);

        assertEquals(biteDontCare, clone);
    }

    @Test
    public void testShiftRight() {
        TribitByte bite = TribitByte.value(0, 0, 0, 0, 1, 1, 1, 1);
        TribitByte lshBite = TribitByte.value(0, 0, 0, 1, 1, 1, 1, 0);
        assertEquals(bite.shiftRight(1), lshBite);
    }

    @Test
    public void testShiftRightLarge() {
        TribitByte bite = TribitByte.value(0, 0, 0, 0, 1, 1, 1, 1);
        assertEquals(bite.shiftLeft(100), TribitByte.ALL_ZERO);
    }

    @Test
    public void testShiftNone() {
        TribitByte bite = TribitByte.value(0, 0, 0, 0, 1, 1, 1, 1);
        assertEquals(bite.shiftLeft(0), bite);
    }
}