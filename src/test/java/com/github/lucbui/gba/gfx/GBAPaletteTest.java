package com.github.lucbui.gba.gfx;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GBAPaletteTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorEmptyList() {
        new GBAPalette(Collections.emptyList());
    }

    @Test
    public void constructorNonEmptyList() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertEquals(1, palette.size());
        assertEquals(GBAColor.BLACK, palette.get(0));
    }

    @Test
    public void constructorCopy() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        GBAPalette otherPalette = new GBAPalette(palette);
        assertEquals(palette, otherPalette);
    }

    @Test(expected = NullPointerException.class)
    public void constructorNull() {
        new GBAPalette((GBAPalette) null);
        new GBAPalette((List<GBAColor>) null);
    }

    @Test
    public void get() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertEquals(GBAColor.BLACK, palette.get(0));
    }

    @Test
    public void getFirst() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertEquals(GBAColor.BLACK, palette.getFirst());
    }

    public void hasColor() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertTrue(palette.hasColor(0));
        assertFalse(palette.hasColor(1));
    }

    @Test
    public void size() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
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