package com.github.lucbui.utility;

import org.junit.Test;

import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.Assert.*;

public class ParseUtilsTest {

    @Test
    public void testValidInteger(){
        OptionalInt one = ParseUtils.parseInt("1", 10);
        assertTrue(one.isPresent());
        assertEquals(one.getAsInt(), 1);
    }

    @Test
    public void testInvalidInteger(){
        OptionalInt none = ParseUtils.parseInt("none", 10);
        assertFalse(none.isPresent());
    }

    @Test
    public void testValidLong(){
        OptionalLong one = ParseUtils.parseLong("1", 10);
        assertTrue(one.isPresent());
        assertEquals(one.getAsLong(), 1);
    }

    @Test
    public void testInvalidLong(){
        OptionalLong none = ParseUtils.parseLong("none", 10);
        assertFalse(none.isPresent());
    }
}