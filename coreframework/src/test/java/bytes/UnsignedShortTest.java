package com.github.lucbui.bytes;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class UnsignedShortTest {

    @Test
    public void valueOfLiteralValid(){
        UnsignedShort bite = UnsignedShort.valueOf(0);
        assertEquals(0, bite.value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfLiteralInvalid(){
        UnsignedShort.valueOf(-1);
    }

    @Test
    public void valueOfLiteralEqual(){
        assertSame(UnsignedShort.valueOf(0), UnsignedShort.valueOf(0));
    }

    @Test
    public void valueOfUnsignedByte(){
        UnsignedByte ub = UnsignedByte.valueOf(0);
        UnsignedShort us = UnsignedShort.valueOf(ub);
        UnsignedShort us2 = UnsignedShort.valueOf(0);
        assertSame(us, us2);
    }

    @Test
    public void compareToSame(){
        assertEquals(0, UnsignedShort.valueOf(0).compareTo(UnsignedShort.valueOf(0)));
    }

    @Test
    public void compareToSmaller(){
        assertTrue(UnsignedShort.valueOf(0).compareTo(UnsignedShort.valueOf(1)) < 0);
    }

    @Test
    public void compareToLarger(){
        assertTrue(UnsignedShort.valueOf(1).compareTo(UnsignedShort.valueOf(0)) > 0);
    }

    @Test(expected = NullPointerException.class)
    public void compareToNull(){
        UnsignedShort.valueOf(0).compareTo(null);
    }

    @Test
    public void equalsSame(){
        assertEquals(UnsignedShort.valueOf(0), UnsignedShort.valueOf(0));
    }

    @Test
    public void equalsByValueSame(){
        assertTrue(UnsignedShort.valueOf(0).equalsByValue(UnsignedWord.valueOf(0)));
    }

    @Test
    public void equalsByValueDifferent(){
        assertFalse(UnsignedShort.valueOf(0).equalsByValue(UnsignedWord.valueOf(1)));
    }

    @Test
    public void equalsByValueNull(){
        assertFalse(UnsignedShort.valueOf(0).equalsByValue(null));
    }

    @Test(expected = ClassCastException.class)
    public void equalsByValueWrongClass(){
        UnsignedShort.valueOf(0).equalsByValue(0);
    }
}