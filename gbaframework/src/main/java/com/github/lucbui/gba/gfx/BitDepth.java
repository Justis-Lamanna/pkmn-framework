package com.github.lucbui.gba.gfx;

/**
 * The bit depth of a tile.
 */
public enum BitDepth {
    /**
     * Encoding where each pixel is 4 bits
     */
    FOUR(16),

    /**
     * Encoding where each pixel is 8 bits
     */
    EIGHT(256);

    private final int numberOfPixels;

    BitDepth(int numberOfPixels){
        this.numberOfPixels = numberOfPixels;
    }

    /**
     * Verify a pixel as valid for this bitDepth.
     * @param pixel
     */
    public void verifyPixel(int pixel) {
        if(pixel < 0 || pixel >= numberOfPixels){
            throw new IndexOutOfBoundsException("Pixel must be between 0 and " + (numberOfPixels - 1));
        }
    }

    /**
     * Get the permissible pixels.
     * @return
     */
    public int getNumberOfPixels(){
        return this.numberOfPixels;
    }
}