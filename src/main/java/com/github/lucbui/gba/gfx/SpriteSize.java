package com.github.lucbui.gba.gfx;

/**
 * Describes legal GBA Sprite Sizes
 */
public enum SpriteSize {
    /**
     * Square Type 0, 8x8
     */
    SQUARE_0(1, 1),
    /**
     * Square Type 1, 16x16
     */
    SQUARE_1(2, 2),
    /**
     * Square Type 2, 32x32
     */
    SQUARE_2(4, 4),
    /**
     * Square Type 3, 64x64
     */
    SQUARE_3(8, 8),
    /**
     * Horizontal Type 0, 16x8
     */
    HORIZONTAL_0(2, 1),
    /**
     * Horizontal Type 1, 32x8
     */
    HORIZONTAL_1(4, 1),
    /**
     * Horizontal Type 2, 32x16
     */
    HORIZONTAL_2(4, 2),
    /**
     * Horizontal Type 3, 64x32
     */
    HORIZONTAL_3(8, 4),
    /**
     * Vertical Type 0, 8x16
     */
    VERTICAL_0(1, 2),
    /**
     * Vertical Type 1, 8x32
     */
    VERTICAL_1(1, 4),
    /**
     * Vertical Type 2, 16x32
     */
    VERTICAL_2(2, 4),
    /**
     * Vertical Type 3, 32x64
     */
    VERTICAL_3(4, 8);

    private final int width;
    private final int height;

    SpriteSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the width of a sprite, in tiles.
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the width of a sprite, in pixels.
     * @return
     */
    public int getWidthInPixels(){
        return getWidth() * GBATile.WIDTH_IN_PIXELS;
    }

    /**
     * Gets the height of a sprite, in tiles.
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the width of a sprite, in pixels.
     * @return
     */
    public int getHeightInPixels(){
        return getHeight() * GBATile.HEIGHT_IN_PIXELS;
    }

    /**
     * Get the total number of tiles needed to make this sprite.
     * @return
     */
    public int getArea(){
        return getWidth() * getHeight();
    }

    /**
     * Get the total number of pixels needed to make this sprite.
     * @return
     */
    public int getAreaInPixels(){
        return getWidthInPixels() * getHeightInPixels();
    }
}
