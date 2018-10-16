package com.github.lucbui.gba.gfx;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.bytes.UnsignedShort;
import com.github.lucbui.file.HexFieldIterator;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * A class which encapsulates a GBAColor
 */
public class GBAColor {

    public static final HexReader<GBAColor> HEX_READER = iterator -> {
        int color = (int) UnsignedShort.valueOf(iterator.get(2)).getValue();
        return new GBAColor(color & 31, (color >>> 5) & 31, (color >>> 10) & 31);
    };

    public static final HexWriter<GBAColor> HEX_WRITER = (object, iterator) -> {
        int color = object.getRed() | (object.getGreen() << 5) | (object.getBlue() << 10);
        iterator.write(UnsignedShort.valueOf(color).toBytes());
    };

    private int red;
    private int green;
    private int blue;

    private GBAColor(int red, int green, int blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Create a GBAColor from a standard color.
     * Alpha is ignored. Red, green, and blue values are rounded to the nearest 8th.
     * @param color The color to turn into
     * @return The converted GBAColor
     */
    public static GBAColor from(Color color){
        Objects.requireNonNull(color, "Color supplied is null");
        return new GBAColor(color.getRed() >>> 3, color.getGreen() >>> 3, color.getBlue() >>> 3);
    }

    /**
     * Create a GBAColor from raw integer values.
     * Red, green, and blue must be between 0 and 31.
     * @param red The red value, 0 - 31
     * @param green The green value, 0 - 31
     * @param blue The blue value, 0 - 31
     * @return The converted GBAColor
     */
    public static GBAColor from(int red, int green, int blue){
        verifyBetweenZeroAndThirtyOne(red, "red");
        verifyBetweenZeroAndThirtyOne(green, "green");
        verifyBetweenZeroAndThirtyOne(blue, "blue");
        return new GBAColor(red, green, blue);
    }

    private static void verifyBetweenZeroAndThirtyOne(int color, String colorName){
        if(color < 0 || color > 31){
            throw new IllegalArgumentException(colorName + " needs to be between 0 and 31. Found: " + color);
        }
    }

    /**
     * Get the red value
     * @return 0-31
     */
    public int getRed() {
        return red;
    }

    /**
     * Get the green value
     * @return 0-31
     */
    public int getGreen() {
        return green;
    }

    /**
     * Get the blue value
     * @return 0-31
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Convert this into a Color which best represents it.
     * @return The color which matches this GBAColor.
     */
    public Color getColor(){
        return new Color(red << 3, green << 3, blue << 3);
    }

    @Override
    public String toString() {
        return "GBAColor{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }
}
