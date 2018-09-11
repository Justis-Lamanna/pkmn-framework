package com.github.lucbui.bytes;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class UnsignedWordTest {

    @Test
    void valueOfLiteralValid(){
        UnsignedWord bite = UnsignedWord.valueOf(0);
        assertEquals(0, bite.value);
    }

    @Test
    void valueOfLiteralInvalid(){
        assertThrows(IllegalArgumentException.class, () -> UnsignedWord.valueOf(-1));
    }

    @Test
    void valueOfLiteralEqual(){
        assertSame(UnsignedWord.valueOf(0), UnsignedWord.valueOf(0));
    }

    @Test
    void valueOfByteBufferValid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0, 0, 0});
        UnsignedWord bite = UnsignedWord.valueOf(bb);
        assertEquals(0, bite.value);
    }

    @Test
    void valueOfByteBufferInvalid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[0]);
        assertThrows(IndexOutOfBoundsException.class, () -> UnsignedWord.valueOf(bb));
    }

    @Test
    void valueOfByteBufferNull(){
        assertThrows(NullPointerException.class, () -> UnsignedWord.valueOf((ByteBuffer)null));
    }

    @Test
    void valueOfUnsignedByte(){
        UnsignedByte ub = UnsignedByte.valueOf(0);
        UnsignedWord us = UnsignedWord.valueOf(ub);
        UnsignedWord us2 = UnsignedWord.valueOf(0);
        assertSame(us, us2);
    }

    @Test
    void compareToSame(){
        assertEquals(0, UnsignedWord.valueOf(0).compareTo(UnsignedWord.valueOf(0)));
    }

    @Test
    void compareToSmaller(){
        assertTrue(UnsignedWord.valueOf(0).compareTo(UnsignedWord.valueOf(1)) < 0);
    }

    @Test
    void compareToLarger(){
        assertTrue(UnsignedWord.valueOf(1).compareTo(UnsignedWord.valueOf(0)) > 0);
    }

    @Test
    void compareToNull(){
        assertThrows(NullPointerException.class, () -> UnsignedWord.valueOf(0).compareTo(null));
    }

    @Test
    void equalsSame(){
        assertEquals(UnsignedWord.valueOf(0), UnsignedWord.valueOf(0));
    }

    @Test
    void equalsDifferent(){
        assertNotEquals(UnsignedWord.valueOf(0), UnsignedWord.valueOf(1));
    }

    @Test
    void equalsNull(){
        assertNotEquals(null, UnsignedWord.valueOf(0));
    }

    @Test
    void equalsByValueSame(){
        assertTrue(UnsignedWord.valueOf(0).equalsByValue(UnsignedByte.valueOf(0)));
    }

    @Test
    void equalsByValueDifferent(){
        assertFalse(UnsignedWord.valueOf(0).equalsByValue(UnsignedByte.valueOf(1)));
    }

    @Test
    void equalsByValueNull(){
        assertFalse(UnsignedWord.valueOf(0).equalsByValue(null));
    }

    @Test
    void equalsByValueWrongClass(){
        assertThrows(ClassCastException.class, () -> UnsignedWord.valueOf(0).equalsByValue(0));
    }
}