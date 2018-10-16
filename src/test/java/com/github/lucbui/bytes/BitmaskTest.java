package com.github.lucbui.bytes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitmaskTest {

    @Test
    void applyBitmask() {
        Bitmask bm = new Bitmask(0b1111);
        int applied = bm.apply(64);
        assertEquals(64 & 0b1111, applied);
    }

    @Test
    void applyBitmaskAndShift(){
        Bitmask bm = new Bitmask(0b1111, 4);
        int applied = bm.apply(64);
        assertEquals((64 & 0b1111) >> 4, applied);
    }
}