package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.*;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.gba.exception.IllegalSizeException;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * A class which encapsulates a GBA Tile.
 */
public class GBATile implements GBAGraphic, Serializable {

    static final long serialVersionUID = 42L;

    /**
     * The width of a tile, in pixels
     */
    public static final int WIDTH_IN_PIXELS = 8;

    /**
     * The height of a tile, in pixels
     */
    public static final int HEIGHT_IN_PIXELS = 8;

    /**
     * The area of a tile, in pixels
     */
    public static final int AREA_IN_PIXELS = WIDTH_IN_PIXELS * HEIGHT_IN_PIXELS;

    /**
     * A blank four-bit tile.
     */
    public static GBATile BLANK_TILE_FOUR_BIT = new GBATile(BitDepth.FOUR);

    /**
     * A blank eight-bit tile.
     */
    public static GBATile BLANK_TILE_EIGHT_BIT = new GBATile(BitDepth.EIGHT);

    private static final Bitmask LEFT_PIXEL_MASK = Bitmask.forBitRange(0, 3);
    private static final Bitmask RIGHT_PIXEL_MASK = Bitmask.forBitRange(4, 7);

    private BitDepth bitDepth;

    private byte[] pixels;

    //Run validation to verify the x/y bounds are inside the tile.
    private static void verifyBounds(int x, int y){
        if(x < 0 || x >= WIDTH_IN_PIXELS){
            throw new IndexOutOfBoundsException("x must be between 0 and " + WIDTH_IN_PIXELS);
        }
        if(y < 0 || y >= HEIGHT_IN_PIXELS){
            throw new IndexOutOfBoundsException("y must be between 0 and " + HEIGHT_IN_PIXELS);
        }
    }

    /**
     * Creates an empty GBATile, filled with 0s
     * @param depth The bitdepth of this tile.
     * @throws NullPointerException depth is null;
     */
    protected GBATile(BitDepth depth){
        this.bitDepth = depth;
        this.pixels = new byte[AREA_IN_PIXELS];
        Arrays.fill(this.pixels, (byte)0);
    }

    /**
     * Create a GBATile from a BitDepth and 64 length array of pixels.
     * @param depth The bit depth to use.
     * @param pixels The pixel values.
     * @throws NullPointerException depth or pixels are null
     * @throws IllegalSizeException Pixel array is not AREA_IN_PIXELS size
     */
    protected GBATile(BitDepth depth, byte[] pixels){
        this.bitDepth = depth;
        this.pixels = pixels;
    }

    /**
     * Get a Hexer that writes GBATiles
     * @param depth The bitDepth to use.
     * @return The created hexer
     */
    public static Hexer<GBATile> getHexer(BitDepth depth){
        Objects.requireNonNull(depth);
        return new Hexer<GBATile>() {
            @Override
            public int getSize(GBATile object) {
                if(object.getBitDepth() == BitDepth.FOUR){
                    return AREA_IN_PIXELS / 2;
                } else {
                    return AREA_IN_PIXELS;
                }
            }

            @Override
            public GBATile read(HexFieldIterator iterator) {
                byte[] pixels = new byte[AREA_IN_PIXELS];
                if(depth == BitDepth.FOUR){
                    //Each byte contains two pixels of info.
                    for(int idx = 0; idx < (AREA_IN_PIXELS / 2); idx++){
                        byte bite = iterator.getRelative(0, 1).get(0);
                        int leftPixel = LEFT_PIXEL_MASK.apply(bite);
                        int rightPixel = RIGHT_PIXEL_MASK.apply(bite);
                        pixels[idx * 2] = HexUtils.unsignedByteToByte(leftPixel);
                        pixels[idx * 2 + 1] = HexUtils.unsignedByteToByte(rightPixel);
                        iterator.advanceRelative(1);
                    }
                    return new GBATile(depth, pixels);
                } else if(depth == BitDepth.EIGHT){
                    //Each bite contains only one pixel of info.
                    for(int idx = 0; idx < AREA_IN_PIXELS; idx++){
                        pixels[idx] = iterator.getByte(0);
                        iterator.advanceRelative(1);
                    }
                    return new GBATile(depth, pixels);
                } else {
                    throw new IllegalArgumentException("Invalid depth specified:" + depth);
                }
            }

            @Override
            public void write(GBATile object, HexFieldIterator iterator) {
                byte[] pixels = object.pixels;
                ByteWindow window = new ByteWindow();
                if (depth == BitDepth.FOUR) {
                    for (int idx = 0; idx < (AREA_IN_PIXELS / 2); idx++) {
                        int pixl = Bitmask.merge()
                                .with(LEFT_PIXEL_MASK, pixels[idx * 2])
                                .with(RIGHT_PIXEL_MASK, pixels[idx * 2 + 1])
                                .apply();
                        byte bite = HexUtils.unsignedByteToByte(pixl);
                        window.set(idx, bite);
                    }
                } else if (depth == BitDepth.EIGHT) {
                    for (int idx = 0; idx < AREA_IN_PIXELS; idx++) {
                        byte bite = HexUtils.unsignedByteToByte(pixels[idx]);
                        window.set(idx, bite);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid depth specified:" + depth);
                }
                iterator.write(window);
                iterator.advanceRelative(window.getRange());
            }
        };
    }

    /**
     * Get the pixel at the specified x/y position.
     * Origin is top-left corner.
     * @param x The x coordinate to retrieve.
     * @param y The y coordinate to retrieve.
     * @return The pixel value at the specified coordinate.
     * @throws IndexOutOfBoundsException x or y is not in the tile space
     */
    public int getPixel(int x, int y){
        verifyBounds(x, y);
        return HexUtils.byteToUnsignedByte(getPixelAsByte(x, y));
    }

    /**
     * Get the pixel at the specified x/y position, as a byte.
     * @param x
     * @param y
     * @return
     */
    protected byte getPixelAsByte(int x, int y){
        return pixels[y * WIDTH_IN_PIXELS + x];
    }

    /**
     * Get a row at the specified y position.
     * @param y The y coordinate to retrieve.
     * @return The pixel values in the specified row.
     * @throws IndexOutOfBoundsException y is not in the tile space
     */
    public int[] getRow(int y){
        verifyBounds(0, y);
        byte[] row = getRowAsBytes(y);
        int[] rowCopy = new int[row.length];
        Arrays.setAll(rowCopy, i -> HexUtils.byteToUnsignedByte(row[i]));
        return rowCopy;
    }

    /**
     * Get a row at the specified y position, as bytes.
     * @param y
     * @return
     */
    protected byte[] getRowAsBytes(int y){
        return Arrays.copyOfRange(pixels, y * WIDTH_IN_PIXELS, (y + 1) * WIDTH_IN_PIXELS);
    }

    /**
     * Get the bit depth of this tile.
     * @return
     */
    public BitDepth getBitDepth(){
        return bitDepth;
    }

    @Override
    public int[] to1DArray(){
        int[] newArray = new int[this.pixels.length];
        Arrays.setAll(newArray, i -> HexUtils.byteToUnsignedByte(pixels[i]));
        return newArray;
    }

    @Override
    public Type getType(){
        return Type.INDEXED;
    }

    @Override
    public int getWidth() {
        return WIDTH_IN_PIXELS;
    }

    @Override
    public int getHeight() {
        return HEIGHT_IN_PIXELS;
    }

    @Override
    public String toString() {
        return "GBATile{" +
                "bitDepth=" + bitDepth +
                ", pixels=" + Arrays.toString(pixels) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GBATile gbaTile = (GBATile) o;
        return bitDepth == gbaTile.bitDepth &&
                Arrays.equals(pixels, gbaTile.pixels);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(bitDepth);
        result = 31 * result + Arrays.hashCode(pixels);
        return result;
    }

    /**
     * Modify this tile.
     * This returns a Creator which modifies the current tile. The tile returned from the creator is an all-new
     * tile built from this one.
     * @return A creator, populated with this tile's information.
     */
    public Creator modify(){
        return new Creator(this);
    }

    /**
     * Create a tile.
     * @param bitDepth The bitDepth of this tile.
     * @return A creator to create a tile.
     */
    public static Creator build(BitDepth bitDepth){
        Objects.requireNonNull(bitDepth);
        return new Creator(bitDepth);
    }

    /**
     * Creator that creates a GBA Tile.
     */
    public static class Creator {
        private BitDepth bitDepth;
        private byte[] pixels;

        private Creator(GBATile tile){
            Objects.requireNonNull(tile);
            this.bitDepth = tile.bitDepth;
            this.pixels = Arrays.copyOf(tile.pixels, tile.pixels.length);
        }

        private Creator(BitDepth bitDepth){
            Objects.requireNonNull(bitDepth);
            this.bitDepth = bitDepth;
            this.pixels = new byte[AREA_IN_PIXELS];
        }

        /**
         * Set the pixel value at the specified x/y coordinate.
         * @param x The x position in the tile.
         * @param y The y position in the tile.
         * @param pixel The pixel value to change to.
         * @return This instance.
         * @throws IndexOutOfBoundsException x or y is out of tile bounds, or specified pixel is too large
         * for the bitDepth.
         */
        public Creator setPixel(int x, int y, int pixel){
            verifyBounds(x, y);
            bitDepth.verifyPixel(pixel);
            this.pixels[y * WIDTH_IN_PIXELS + x] = HexUtils.unsignedByteToByte(pixel);
            return this;
        }

        /**
         * Create a GBATile.
         * @return The new GBATile.
         */
        public GBATile create(){
            return new GBATile(bitDepth, pixels);
        }
    }
}
