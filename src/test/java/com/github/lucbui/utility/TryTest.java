package com.github.lucbui.utility;

import org.junit.Test;

import static org.junit.Assert.*;

public class TryTest {

    @Test
    public void nonNullValid() {
        Try<Integer> tri = Try.ok(1);
        assertTrue(tri.isOk());
        assertFalse(tri.isError());
    }

    @Test
    public void nullValid() {
        Try<Integer> tri = Try.ok(null);
        assertTrue(tri.isOk());
        assertFalse(tri.isError());
    }

    @Test
    public void error() {
        Try<Integer> tri = Try.error("");
        assertTrue(tri.isError());
        assertFalse(tri.isOk());
    }

    @Test
    public void thro() {
        Try<Integer> tri = Try.ok(1);
        int value = tri.or(RuntimeException::new);
        assertEquals(value, 1);
    }

    @Test
    public void runningValid() {
        Try<Integer> tri = Try.running(() -> 1, "");
        assertEquals(tri.get().intValue(), 1);
    }

    @Test(expected = RuntimeException.class)
    public void runningInvalid() {
        Try.running(() -> {
            throw new RuntimeException();
        }, "").or(RuntimeException::new);
    }
}