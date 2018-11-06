package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.Bitmask;
import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexUtils;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.file.HexFieldIterator;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * A class which encapsulates a GBA Tile.
 */
public class GBATile implements GBAGraphic{

    private static final Bitmask LEFT_PIXEL_MASK = new Bitmask(0b1111);
    private static final Bitmask RIGHT_PIXEL_MASK = new Bitmask(0b11110000, 4);

    private BitDepth bitDepth;

    private int[] pixels;

    /**
     * Creates an empty GBATile, filled with 0s
     * @param depth The bitdepth of this tile.
     */
    public GBATile(BitDepth depth){
        Objects.requireNonNull(depth);
        this.bitDepth = depth;
        this.pixels = new int[64];
        Arrays.fill(this.pixels, 0);
    }

    /**
     * Create a GBATile from a BitDepth and 64 length array of pixels.
     * @param depth The bit depth to use.
     * @param pixels The pixel values.
     */
    public GBATile(BitDepth depth, int[] pixels){
        Objects.requireNonNull(pixels);
        Objects.requireNonNull(depth);
        if(pixels.length != 64){
            throw new IllegalArgumentException("pixels must be length 64");
        }
        this.bitDepth = depth;
        this.pixels = pixels;
    }

    /**
     * A hex reader which reads a GBATile
     * @param depth The bit depth to use.
     * @return The Hex Reader to use.
     */
    public static HexReader<GBATile> getHexReader(BitDepth depth){
        return iterator -> {
            int[] pixels = new int[64];
            if(depth == BitDepth.FOUR){
                //Each byte contains two pixels of info.
                for(int idx = 0; idx < 32; idx++){
                    byte bite = iterator.getRelative(0, 1).get(0);
                    int leftPixel = LEFT_PIXEL_MASK.apply(bite);
                    int rightPixel = RIGHT_PIXEL_MASK.apply(bite);
                    pixels[idx * 2] = leftPixel;
                    pixels[idx * 2 + 1] = rightPixel;
                    iterator.advanceRelative(1);
                }
                return new GBATile(depth, pixels);
            } else if(depth == BitDepth.EIGHT){
                //Each bite contains only one pixel of info.
                for(int idx = 0; idx < 64; idx++){
                    byte bite = iterator.getRelative(0, 1).get(0);
                    pixels[idx] = HexUtils.byteToUnsignedByte(bite);
                    iterator.advanceRelative(1);
                }
                return new GBATile(depth, pixels);
            } else {
                throw new IllegalArgumentException("Invalid depth specified:" + depth);
            }
        };
    }

    /**
     * A hex writer which can write a GBATile
     */
    public static HexWriter<GBATile> getHexWriter(BitDepth depth) {
        return (object, iterator) -> {
            int[] pixels = object.pixels;
            if (depth == BitDepth.FOUR) {
                for (int idx = 0; idx < 32; idx++) {
                    int pixl = Bitmask.merge()
                            .with(LEFT_PIXEL_MASK, pixels[idx * 2])
                            .with(RIGHT_PIXEL_MASK, pixels[idx * 2 + 1])
                            .apply();
                    byte bite = HexUtils.unsignedByteToByte(pixl);
                    ByteBuffer bb = ByteBuffer.wrap(new byte[]{bite});
                    iterator.writeRelative(0, bb);
                    iterator.advanceRelative(1);
                }
            } else if (depth == BitDepth.EIGHT) {
                for (int idx = 0; idx < 64; idx++) {
                    byte bite = HexUtils.unsignedByteToByte(pixels[idx]);
                    ByteBuffer bb = ByteBuffer.wrap(new byte[]{bite});
                    iterator.writeRelative(0, bb);
                    iterator.advanceRelative(1);
                }
            } else {
                throw new IllegalArgumentException("Invalid depth specified:" + depth);
            }
        };
    }

    /**
     * Get the pixel at the speciifed x/y position.
     * Origin is top-left corner.
     * @param x
     * @param y
     * @return
     */
    public int getPixelAt(int x, int y){
        return pixels[y * 8 + x];
    }

    /**
     * Get a row at the specified y position.
     * @param y
     * @return
     */
    public int[] getRowAt(int y){
        return Arrays.copyOfRange(pixels, y * 8, (y + 1) * 8);
    }

    /**
     * Set the pixel at the specified x/y position.
     * @param x
     * @param y
     * @param pxl
     */
    public void setPixelAt(int x, int y, int pxl){
        pixels[y * 8 + x] = pxl;
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
        return Arrays.copyOf(this.pixels, pixels.length);
    }

    @Override
    public Type getType(){
        return Type.INDEXED;
    }

    @Override
    public int getWidth() {
        return 8;
    }

    @Override
    public int getHeight() {
        return 8;
    }

    @Override
    public String toString() {
        return "GBATile{" +
                "bitDepth=" + bitDepth +
                ", pixels=" + Arrays.toString(pixels) +
                '}';
    }
}
