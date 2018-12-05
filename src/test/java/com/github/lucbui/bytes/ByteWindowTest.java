package com.github.lucbui.bytes;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteWindowTest {

    private static ByteWindow createWindow(int from, int to){
        ByteWindow bw = new ByteWindow();
        for(int idx = from; idx < to; idx++){
            bw.set(idx, (byte)idx);
        }
        return bw;
    }

    @Test
    public void noParamConstructor() {
        ByteWindow bw = new ByteWindow();
        assertEquals(0, bw.getNumberOfBytes());
        assertEquals(0, bw.getRange());
        assertTrue(bw.hasNoHoles());
    }

    @Test
    public void setValues() {
        ByteWindow bw = createWindow(0, 5);
        assertEquals(5, bw.getNumberOfBytes());
        assertEquals(5, bw.getRange());
        assertTrue(bw.hasNoHoles());
    }

    @Test
    public void hasHoles() {
        ByteWindow bw = new ByteWindow();
        bw.set(0, (byte)0);
        bw.set(5, (byte)5);
        assertFalse(bw.hasNoHoles());
    }

    @Test
    public void subwindowTest() {
        ByteWindow bw = createWindow(0, 5);
        ByteWindow sbw = bw.subWindow(1, 3);
        assertEquals(2, sbw.getNumberOfBytes());
        assertEquals(2, sbw.getRange());
    }

    @Test(expected = IllegalArgumentException.class)
    public void subwindowTestBad() {
        ByteWindow bw = createWindow(0, 5);
        ByteWindow sbw = bw.subWindow(3, 1);
    }
}