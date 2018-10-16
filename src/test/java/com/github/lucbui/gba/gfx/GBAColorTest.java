package com.github.lucbui.gba.gfx;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class GBAColorTest {

    @Test
    void ofColorValid() {
        GBAColor color = GBAColor.from(Color.BLUE);
        assertEquals(color.getRed(), 0);
        assertEquals(color.getGreen(), 0);
        assertEquals(color.getBlue(), 31);
    }

    @Test
    void ofColorInvalid() {
        assertThrows(NullPointerException.class, () -> GBAColor.from(null));
    }

    @Test
    void ofColorNumbersValid(){
        GBAColor color = GBAColor.from(0, 10, 31);
        assertEquals(0, color.getRed());
        assertEquals(10, color.getGreen());
        assertEquals(31, color.getBlue());
    }

    @Test
    void ofColorNumbersInvalid(){
        assertThrows(IllegalArgumentException.class, () -> GBAColor.from(60, 0, 0));
    }

    @Test
    void toColor() {
        Color origColor = new Color(24, 24, 24);
        GBAColor color = GBAColor.from(origColor);
        assertEquals(color.getColor(), origColor);
    }

    @Test
    void toColorNotDivisibleBy8() {
        Color origColor = new Color(27, 27, 27);
        Color origColorDivBy8 = new Color(24, 24, 24);
        GBAColor color = GBAColor.from(origColor);
        assertNotEquals(color.getColor(), origColor);
        assertEquals(color.getColor(), origColorDivBy8);
    }
}