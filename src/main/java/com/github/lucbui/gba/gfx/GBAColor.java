package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.Bitmask;
import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.bytes.UnsignedShort;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A class which encapsulates a GBAColor
 */
public class GBAColor implements Serializable {

    static final long serialVersionUID = 42L;

    private static final Bitmask RED_BITMASK = Bitmask.forBitRange(0, 4);
    private static final Bitmask GREEN_BITMASK = Bitmask.forBitRange(5, 9);
    private static final Bitmask BLUE_BITMASK = Bitmask.forBitRange(10, 14);

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
     * @param red The red size, 0 - 31
     * @param green The green size, 0 - 31
     * @param blue The blue size, 0 - 31
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
     * Get the red size
     * @return 0-31
     */
    public int getRed() {
        return red;
    }

    /**
     * Get the green size
     * @return 0-31
     */
    public int getGreen() {
        return green;
    }

    /**
     * Get the blue size
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

    /**
     * Tint this color.
     * The current red, green, and blue channels are multipled by redAmt, greenAmt,
     * and blueAmt, respectively, and combined into a new color. If any color exceeds the 0-31
     * range, it is clamped to 0 or 31, depending. The following breaks down the multipliers and their effects:
     * * amount <= 0 - Turns the color black
     * * 0 < amount < 1 - Darkens the color. Numbers closer to 0 result in darker colors.
     * * amount = 1 - No change occurs
     * * amount > 1 - Lightens the color. The greater the number, the more white the color becomes.
     * @param redAmt The amount to tint the red channel.
     * @param greenAmt The amount to tint the green channel.
     * @param blueAmt The amount to tint the blue channel.
     * @return The tinted colors
     */
    public GBAColor tint(float redAmt, float greenAmt, float blueAmt){
        int newRed = (int)(red * redAmt);
        int newGreen = (int)(green * greenAmt);
        int newBlue = (int)(blue * blueAmt);
        return new GBAColor(
                clamp(newRed, 0, 31),
                clamp(newGreen, 0, 31),
                clamp(newBlue, 0, 31));
    }

    /**
     * Tint this color
     * The red, green, and blue channels are all multipled by amount, and combined into a new color.
     * If any color exceeds the 0-31 range, it is clamped to 0 or 31, depending.
     * The following breaks down the multipliers and their effects:
     * * amount <= 0 - Turns the color black
     * * 0 < amount < 1 - Darkens the color. Numbers closer to 0 result in darker colors.
     * * amount = 1 - No change occurs
     * * amount > 1 - Lightens the color. The greater the number, the more white the color becomes.
     * @param amount The amount to tint all channels.
     * @return
     */
    public GBAColor tint(float amount){
        return tint(amount, amount, amount);
    }

    /**
     * Darkens this color by 10%
     * @return
     */
    public GBAColor darken(){
        return tint(0.9f, 0.9f, 0.9f);
    }

    /**
     * Lightens the color by 10%
     * @return
     */
    public GBAColor lighten(){
        return tint(1.1f, 1.1f, 1.1f);
    }

    //Why doesn't this exist standardly smh.
    private int clamp(int num, int lower, int upper){
        if(num < lower){
            return lower;
        } else if(num > upper){
            return upper;
        } else {
            return num;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GBAColor gbaColor = (GBAColor) o;
        return red == gbaColor.red &&
                green == gbaColor.green &&
                blue == gbaColor.blue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue);
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
