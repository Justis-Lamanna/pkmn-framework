package com.github.lucbui.gba.gfx;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GBAPaletteTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorEmptyList() {
        GBAPalette.create().build();
    }

    @Test
    public void constructorNonEmptyList() {
        GBAPalette palette = GBAPalette.create().with(GBAColor.BLACK).build();
        assertEquals(1, palette.size());
        assertEquals(GBAColor.BLACK, palette.get(0));
    }

    @Test
    public void constructorCopy() {
        GBAPalette palette = GBAPalette.create().with(GBAColor.BLACK).build();
        GBAPalette otherPalette = palette.modify().build();
        assertEquals(palette, otherPalette);
    }

    @Test(expected = NullPointerException.class)
    public void constructorNull() {
        GBAPalette.create().with((GBAColor)null).build();
        GBAPalette.create().with((GBAPalette) null).build();
    }

    @Test
    public void get() {
        GBAPalette palette = GBAPalette.create().with(GBAColor.BLACK).build();;
        assertEquals(GBAColor.BLACK, palette.get(0));
    }

    @Test
    public void getFirst() {
        GBAPalette palette = GBAPalette.create().with(GBAColor.BLACK).build();
        assertEquals(GBAColor.BLACK, palette.getFirst());
    }

    public void hasColor() {
        GBAPalette palette = GBAPalette.create().with(GBAColor.BLACK).build();
        assertTrue(palette.hasColor(0));
        assertFalse(palette.hasColor(1));
    }

    @Test
    public void size() {
        GBAPalette palette = GBAPalette.create().with(GBAColor.BLACK).build();
        assertEquals(1, palette.size());
    }

    @Test
    public void buildWithOne() {
        GBAPalette palette = GBAPalette.create().with(GBAColor.BLACK).build();
        assertEquals(1, palette.size());
        assertEquals(GBAColor.BLACK, palette.getFirst());
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWithNone() {
        GBAPalette.create().build();
    }
}