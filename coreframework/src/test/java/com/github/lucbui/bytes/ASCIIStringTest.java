package com.github.lucbui.bytes;

import com.github.lucbui.utility.Try;
import org.junit.Test;

import static org.junit.Assert.*;

public class ASCIIStringTest {

    @Test
    public void ofValidString() {
        Try<ASCIIString> string = ASCIIString.of("Hello World");
        assertTrue(string.isOk());
        assertEquals("Hello World", string.get().getString());
    }

    @Test
    public void ofInvalidString() {
        Try<ASCIIString> string = ASCIIString.of("Héllo World");
        assertTrue(string.isError());
    }

    @Test
    public void toNullString() {
        Try<ASCIIString> string = ASCIIString.of((String) null);
        assertTrue(string.isError());
    }
}