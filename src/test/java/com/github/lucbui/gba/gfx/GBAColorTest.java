package com.github.lucbui.gba.gfx;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class GBAColorTest {

    @Test
    public void ofColorValid() {
        GBAColor color = GBAColor.from(Color.BLUE);
        assertEquals(color.getRed(), 0);
        assertEquals(color.getGreen(), 0);
        assertEquals(color.getBlue(), 31);
    }

    @Test(expected = NullPointerException.class)
    public void ofColorInvalid() {
        GBAColor.from(null);
    }

    @Test
    public void ofColorNumbersValid(){
        GBAColor color = GBAColor.from(0, 10, 31);
        assertEquals(0, color.getRed());
        assertEquals(10, color.getGreen());
        assertEquals(31, color.getBlue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofColorNumbersInvalid(){
        GBAColor.from(60, 0, 0);
    }

    @Test
    public void toColor() {
        Color origColor = new Color(24, 24, 24);
        GBAColor color = GBAColor.from(origColor);
        assertEquals(color.getColor(), origColor);
    }

    @Test
    public void toColorNotDivisibleBy8() {
        Color origColor = new Color(27, 27, 27);
        Color origColorDivBy8 = new Color(24, 24, 24);
        GBAColor color = GBAColor.from(origColor);
        assertEquals(color.getColor(), origColorDivBy8);
    }
}