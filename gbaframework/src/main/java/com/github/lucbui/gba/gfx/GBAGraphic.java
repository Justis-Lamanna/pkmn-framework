package com.github.lucbui.gba.gfx;

/**
 * Common interface to GBA Graphical elements
 */
public interface GBAGraphic {
    /**
     * Convert this image to a 1-dimensional array of pixels.
     * Each pixel runs from left to right, top to bottom.
     * @return The pixels
     */
    int[] to1DArray();

    /**
     * Get the type of GBA this graphic is.
     * @return The type of graphic.
     */
    Type getType();

    /**
     * Get the width of this graphic in pixels.
     * @return
     */
    int getWidth();

    /**
     * Get the height of this graphic in pixels.
     * @return
     */
    int getHeight();

    /**
     * An enum describing a type of graphic.
     */
    enum Type{
        /**
         * An indexed graphic. Pixels correspond to slots on a GBA Palette.
         */
        INDEXED,

        /**
         * A custom image, which describes its own handling.
         */
        CUSTOM
    }
}