package com.github.lucbui.gba.gfx;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GBAPaletteTest {
    @Test
    void constructorEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> new GBAPalette(Collections.emptyList()));
    }

    @Test
    void constructorNonEmptyList() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertEquals(1, palette.size());
        assertEquals(GBAColor.BLACK, palette.get(0));
    }

    @Test
    void constructorCopy() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        GBAPalette otherPalette = new GBAPalette(palette);
        assertEquals(palette, otherPalette);
    }

    @Test
    void constructorNull() {
        assertThrows(NullPointerException.class, () -> new GBAPalette((GBAPalette) null));
        assertThrows(NullPointerException.class, () -> new GBAPalette((List<GBAColor>) null));
    }

    @Test
    void get() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertEquals(GBAColor.BLACK, palette.get(0));
    }

    @Test
    void getNegativeSlot() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertThrows(IllegalArgumentException.class, () -> palette.get(-1));
    }

    @Test
    void getFirst() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertEquals(GBAColor.BLACK, palette.getFirst());
    }

    @Test
    void set() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        palette.set(0, GBAColor.BLUE);
        assertEquals(palette.get(0), GBAColor.BLUE);
    }

    @Test
    void setNegativeSlot() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertThrows(IllegalArgumentException.class, () -> palette.set(-1, GBAColor.BLUE));
    }

    @Test
    void setNullColor() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertThrows(NullPointerException.class, () -> palette.set(0, null));
    }

    @Test
    void hasColor() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertTrue(palette.hasColor(0));
        assertFalse(palette.hasColor(1));
        assertThrows(IllegalArgumentException.class, () -> palette.hasColor(-1));
    }

    @Test
    void size() {
        GBAPalette palette = new GBAPalette(Collections.singletonList(GBAColor.BLACK));
        assertEquals(1, palette.size());
    }

    @Test
    void buildWithOne() {
        GBAPalette palette = GBAPalette.builder().with(GBAColor.BLACK).build();
        assertEquals(1, palette.size());
        assertEquals(GBAColor.BLACK, palette.getFirst());
    }

    @Test
    void buildWithNone() {
        assertThrows(IllegalArgumentException.class, () -> GBAPalette.builder().build());
    }
}