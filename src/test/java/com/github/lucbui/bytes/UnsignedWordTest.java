package com.github.lucbui.bytes;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class UnsignedWordTest {

    @Test
    public void valueOfLiteralValid(){
        UnsignedWord bite = UnsignedWord.valueOf(0);
        assertEquals(0, bite.value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfLiteralInvalid(){
        UnsignedWord.valueOf(-1);
    }

    @Test
    public void valueOfLiteralEqual(){
        assertSame(UnsignedWord.valueOf(0), UnsignedWord.valueOf(0));
    }

    @Test
    public void valueOfByteBufferValid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0, 0, 0});
        UnsignedWord bite = UnsignedWord.valueOf(bb);
        assertEquals(0, bite.value);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void valueOfByteBufferInvalid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[0]);
        UnsignedWord.valueOf(bb);
    }

    @Test(expected = NullPointerException.class)
    public void valueOfByteBufferNull(){
        UnsignedWord.valueOf((ByteBuffer)null);
    }

    @Test
    public void valueOfUnsignedByte(){
        UnsignedByte ub = UnsignedByte.valueOf(0);
        UnsignedWord us = UnsignedWord.valueOf(ub);
        UnsignedWord us2 = UnsignedWord.valueOf(0);
        assertSame(us, us2);
    }

    @Test
    public void compareToSame(){
        assertEquals(0, UnsignedWord.valueOf(0).compareTo(UnsignedWord.valueOf(0)));
    }

    @Test
    public void compareToSmaller(){
        assertTrue(UnsignedWord.valueOf(0).compareTo(UnsignedWord.valueOf(1)) < 0);
    }

    @Test
    public void compareToLarger(){
        assertTrue(UnsignedWord.valueOf(1).compareTo(UnsignedWord.valueOf(0)) > 0);
    }

    @Test(expected = NullPointerException.class)
    public void compareToNull(){
        UnsignedWord.valueOf(0).compareTo(null);
    }

    @Test
    public void equalsSame(){
        assertEquals(UnsignedWord.valueOf(0), UnsignedWord.valueOf(0));
    }

    @Test
    public void equalsByValueSame(){
        assertTrue(UnsignedWord.valueOf(0).equalsByValue(UnsignedByte.valueOf(0)));
    }

    @Test
    public void equalsByValueDifferent(){
        assertFalse(UnsignedWord.valueOf(0).equalsByValue(UnsignedByte.valueOf(1)));
    }

    @Test
    public void equalsByValueNull(){
        assertFalse(UnsignedWord.valueOf(0).equalsByValue(null));
    }

    @Test(expected = ClassCastException.class)
    public void equalsByValueWrongClass(){
        UnsignedWord.valueOf(0).equalsByValue(0);
    }
}