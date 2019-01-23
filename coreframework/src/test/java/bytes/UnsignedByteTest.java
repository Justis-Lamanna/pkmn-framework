package com.github.lucbui.bytes;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class UnsignedByteTest {

    @Test
    public void valueOfLiteralValid(){
        UnsignedByte bite = UnsignedByte.valueOf(0);
        assertEquals(0, bite.value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfLiteralInvalid(){
        UnsignedByte.valueOf(-1);
    }

    @Test
    public void valueOfLiteralEqual(){
        assertSame(UnsignedByte.valueOf(0), UnsignedByte.valueOf(0));
    }

    @Test
    public void valueOfByteBufferValid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0});
        UnsignedByte bite = UnsignedByte.valueOf(bb);
        assertEquals(0, bite.value);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void valueOfByteBufferInvalid(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[0]);
        UnsignedByte.valueOf(bb);
    }

    @Test(expected = NullPointerException.class)
    public void valueOfByteBufferNull(){
        UnsignedByte.valueOf((ByteWindow)null);
    }

    @Test
    public void compareToSame(){
        assertEquals(0, UnsignedByte.valueOf(0).compareTo(UnsignedByte.valueOf(0)));
    }

    @Test
    public void compareToSmaller(){
        assertTrue(UnsignedByte.valueOf(0).compareTo(UnsignedByte.valueOf(1)) < 0);
    }

    @Test
    public void compareToLarger(){
        assertTrue(UnsignedByte.valueOf(1).compareTo(UnsignedByte.valueOf(0)) > 0);
    }

    @Test(expected = NullPointerException.class)
    public void compareToNull(){
        UnsignedByte.valueOf(0).compareTo(null);
    }

    @Test
    public void equalsSame(){
        assertEquals(UnsignedByte.valueOf(0), UnsignedByte.valueOf(0));
    }

    @Test
    public void equalsByValueSame(){
        assertTrue(UnsignedByte.valueOf(0).equalsByValue(UnsignedShort.valueOf(0)));
    }

    @Test
    public void equalsByValueDifferent(){
        assertFalse(UnsignedByte.valueOf(0).equalsByValue(UnsignedShort.valueOf(1)));
    }

    @Test
    public void equalsByValueNull(){
        assertFalse(UnsignedByte.valueOf(0).equalsByValue(null));
    }

    @Test(expected = ClassCastException.class)
    public void equalsByValueWrongClass(){
        UnsignedByte.valueOf(0).equalsByValue(0);
    }
}