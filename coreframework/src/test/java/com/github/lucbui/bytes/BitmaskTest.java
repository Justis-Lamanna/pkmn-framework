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

    @Test
    public void createBitRangeTest(){
        Bitmask bm = new Bitmask(0b11110000, 4);
        Bitmask bm2 = Bitmask.forBitRange(4, 7);
        assertEquals(bm, bm2);
    }

    @Test
    public void createBitTest(){
        Bitmask bm = new Bitmask(0b10000, 4);
        Bitmask bm2 = Bitmask.forBit(4);
        assertEquals(bm, bm2);
    }
}