package com.github.lucbui.bytes;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class UnsignedByteTest {

    @Test
    void valueOfLiteralValid(){
        UnsignedByte bite = UnsignedByte.valueOf(0);
        assertEquals(0, bite.value);
    }

    @Test
    void valueOfLiteralInvalid(){
        assertThrows(IllegalArgumentException.class, () -> UnsignedByte.valueOf(-1));
    }

    @Test
    void valueOfLiteralEqual(){
        assertSame(UnsignedByte.valueOf(0), UnsignedByte.valueOf(0));
    }

    @Test
    void valueOfByteBufferValid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0});
        UnsignedByte bite = UnsignedByte.valueOf(bb);
        assertEquals(0, bite.value);
    }

    @Test
    void valueOfByteBufferInvalid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[0]);
        assertThrows(IndexOutOfBoundsException.class, () -> UnsignedByte.valueOf(bb));
    }

    @Test
    void valueOfByteBufferNull(){
        assertThrows(NullPointerException.class, () -> UnsignedByte.valueOf(null));
    }

    @Test
    void compareToSame(){
        assertEquals(0, UnsignedByte.valueOf(0).compareTo(UnsignedByte.valueOf(0)));
    }

    @Test
    void compareToSmaller(){
        assertTrue(UnsignedByte.valueOf(0).compareTo(UnsignedByte.valueOf(1)) < 0);
    }

    @Test
    void compareToLarger(){
        assertTrue(UnsignedByte.valueOf(1).compareTo(UnsignedByte.valueOf(0)) > 0);
    }

    @Test
    void compareToNull(){
        assertThrows(NullPointerException.class, () -> UnsignedByte.valueOf(0).compareTo(null));
    }

    @Test
    void equalsSame(){
        assertEquals(UnsignedByte.valueOf(0), UnsignedByte.valueOf(0));
    }

    @Test
    void equalsDifferent(){
        assertNotEquals(UnsignedByte.valueOf(0), UnsignedByte.valueOf(1));
    }

    @Test
    void equalsNull(){
        assertNotEquals(null, UnsignedByte.valueOf(0));
    }

    @Test
    void equalsByValueSame(){
        assertTrue(UnsignedByte.valueOf(0).equalsByValue(UnsignedShort.valueOf(0)));
    }

    @Test
    void equalsByValueDifferent(){
        assertFalse(UnsignedByte.valueOf(0).equalsByValue(UnsignedShort.valueOf(1)));
    }

    @Test
    void equalsByValueNull(){
        assertFalse(UnsignedByte.valueOf(0).equalsByValue(null));
    }

    @Test
    void equalsByValueWrongClass(){
        assertThrows(ClassCastException.class, () -> UnsignedByte.valueOf(0).equalsByValue(0));
    }
}