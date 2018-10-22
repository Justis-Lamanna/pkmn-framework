package com.github.lucbui.bytes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitmaskTest {

    @Test
    public void applyBitmask() {
        Bitmask bm = new Bitmask(0b1111);
        int applied = bm.apply(64);
        assertEquals(64 & 0b1111, applied);
    }

    @Test
    public void applyBitmaskAndShift(){
        Bitmask bm = new Bitmask(0b1111, 4);
        int applied = bm.apply(64);
        assertEquals((64 & 0b1111) >> 4, applied);
    }
}