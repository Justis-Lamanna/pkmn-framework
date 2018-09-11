package com.github.lucbui.bytes;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class UnsignedShortTest {

    @Test
    void valueOfLiteralValid(){
        UnsignedShort bite = UnsignedShort.valueOf(0);
        assertEquals(0, bite.value);
    }

    @Test
    void valueOfLiteralInvalid(){
        assertThrows(IllegalArgumentException.class, () -> UnsignedShort.valueOf(-1));
    }

    @Test
    void valueOfLiteralEqual(){
        assertSame(UnsignedShort.valueOf(0), UnsignedShort.valueOf(0));
    }

    @Test
    void valueOfByteBufferValid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0});
        UnsignedShort bite = UnsignedShort.valueOf(bb);
        assertEquals(0, bite.value);
    }

    @Test
    void valueOfByteBufferInvalid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[0]);
        assertThrows(IndexOutOfBoundsException.class, () -> UnsignedShort.valueOf(bb));
    }

    @Test
    void valueOfByteBufferNull(){
        assertThrows(NullPointerException.class, () -> UnsignedShort.valueOf((ByteBuffer)null));
    }

    @Test
    void valueOfUnsignedByte(){
        UnsignedByte ub = UnsignedByte.valueOf(0);
        UnsignedShort us = UnsignedShort.valueOf(ub);
        UnsignedShort us2 = UnsignedShort.valueOf(0);
        assertSame(us, us2);
    }

    @Test
    void compareToSame(){
        assertEquals(0, UnsignedShort.valueOf(0).compareTo(UnsignedShort.valueOf(0)));
    }

    @Test
    void compareToSmaller(){
        assertTrue(UnsignedShort.valueOf(0).compareTo(UnsignedShort.valueOf(1)) < 0);
    }

    @Test
    void compareToLarger(){
        assertTrue(UnsignedShort.valueOf(1).compareTo(UnsignedShort.valueOf(0)) > 0);
    }

    @Test
    void compareToNull(){
        assertThrows(NullPointerException.class, () -> UnsignedShort.valueOf(0).compareTo(null));
    }

    @Test
    void equalsSame(){
        assertEquals(UnsignedShort.valueOf(0), UnsignedShort.valueOf(0));
    }

    @Test
    void equalsDifferent(){
        assertNotEquals(UnsignedShort.valueOf(0), UnsignedShort.valueOf(1));
    }

    @Test
    void equalsNull(){
        assertNotEquals(null, UnsignedShort.valueOf(0));
    }

    @Test
    void equalsByValueSame(){
        assertTrue(UnsignedShort.valueOf(0).equalsByValue(UnsignedWord.valueOf(0)));
    }

    @Test
    void equalsByValueDifferent(){
        assertFalse(UnsignedShort.valueOf(0).equalsByValue(UnsignedWord.valueOf(1)));
    }

    @Test
    void equalsByValueNull(){
        assertFalse(UnsignedShort.valueOf(0).equalsByValue(null));
    }

    @Test
    void equalsByValueWrongClass(){
        assertThrows(ClassCastException.class, () -> UnsignedShort.valueOf(0).equalsByValue(0));
    }
}