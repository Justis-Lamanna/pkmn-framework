package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.Bitmask;
import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.bytes.UnsignedShort;

import java.awt.*;
import java.util.Objects;

/**
 * A class which encapsulates a GBAColor
 */
public class GBAColor {

    private static final Bitmask RED_BITMASK = new Bitmask(0b11111,0);
    private static final Bitmask GREEN_BITMASK = new Bitmask(0b1111100000, 5);
    private static final Bitmask BLUE_BITMASK = new Bitmask(0b111110000000000, 10);

    private static final int MIN_COLOR = 0;
    private static final int MAX_COLOR = 31;

    /**
     * A HexReader for reading GBAColors.
     */
    public static final HexReader<GBAColor> HEX_READER = iterator -> {
        int color = (int) UnsignedShort.valueOf(iterator.get(2)).getValue();
        return new GBAColor(RED_BITMASK.apply(color), GREEN_BITMASK.apply(color), BLUE_BITMASK.apply(color));
    };

    /**
     * A HexWriter for writing GBAColors.
     */
    public static final HexWriter<GBAColor> HEX_WRITER = (object, iterator) -> {
        int color = Bitmask.merge().with(RED_BITMASK, object.red).with(GREEN_BITMASK, object.green).with(BLUE_BITMASK, object.blue).apply();
        iterator.write(UnsignedShort.valueOf(color).toBytes());
    };

    public static GBAColor BLACK = new GBAColor(0, 0, 0);
    public static GBAColor RED = new GBAColor(31, 0, 0);
    public static GBAColor GREEN = new GBAColor(0, 31, 0);
    public static GBAColor BLUE = new GBAColor(0, 0, 31);
    public static GBAColor YELLOW = new GBAColor(31, 31, 0);
    public static GBAColor CYAN = new GBAColor(0, 31, 31);
    public static GBAColor MAGENTA = new GBAColor(31, 0, 31);
    public static GBAColor WHITE = new GBAColor(31, 31, 31);

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
        if(color < MIN_COLOR || color > MAX_COLOR){
            throw new IllegalArgumentException(colorName + " needs to be between " + MIN_COLOR + " and " + MAX_COLOR + ". Found: " + color);
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
