package com.github.lucbui.gba.gfx;

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
public class GBATile {

    /**
     * The bit depth of a tile.
     */
    public enum BitDepth {
        /**
         * Encoding where each pixel is 4 bits
         */
        FOUR,

        /**
         * Encoding where each pixel is 8 bits
         */
        EIGHT
    }

    private BitDepth bitDepth;

    private int[] pixels;

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
     * Create a GBATile from a BitDepth and an 8x8 map of pixels.
     * @param depth The bit depth to use.
     * @param pixels The pixel values.
     */
    public GBATile(BitDepth depth, int[][] pixels){
        Objects.requireNonNull(pixels);
        Objects.requireNonNull(depth);
        if(pixels.length != 8 && pixels[0].length != 8){
            throw new IllegalArgumentException("pixel map must be 8 by 8");
        }
        this.pixels = new int[64];
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                //int pxl = pixels[y * 8 + x];
                //array2d[y][x] = pxl;
                int pxl = pixels[y][x];
                this.pixels[y * 8 + x] = pxl;
            }
        }
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
                    byte bite = iterator.getRelative(idx, 1).get(0);
                    int leftPixel = bite & 0b00001111;
                    int rightPixel = (bite & 0b11110000) >>> 4;
                    pixels[idx * 2] = leftPixel;
                    pixels[idx * 2 + 1] = rightPixel;
                }
                return new GBATile(depth, pixels);
            } else if(depth == BitDepth.EIGHT){
                //Each bite contains only one pixel of info.
                for(int idx = 0; idx < 64; idx++){
                    byte bite = iterator.getRelative(idx, 1).get(0);
                    pixels[idx] = HexUtils.byteToUnsignedByte(bite);
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
    public static HexWriter<GBATile> HEX_WRITER = (object, iterator) -> {
            int[] pixels = object.pixels;
            BitDepth depth = object.bitDepth;
            if(depth == BitDepth.FOUR){
                for(int idx = 0; idx < 32; idx++){
                    int leftPixel = pixels[idx * 2] & 0b1111;
                    int rightPixel = pixels[idx * 2 + 1] & 0b1111;
                    byte bite = HexUtils.unsignedByteToByte(leftPixel | (rightPixel << 4));
                    ByteBuffer bb = ByteBuffer.wrap(new byte[]{bite});
                    iterator.writeRelative(idx, bb);
                }
            } else if(depth == BitDepth.EIGHT){
                for(int idx = 0; idx < 64; idx++){
                    byte bite = HexUtils.unsignedByteToByte(pixels[idx]);
                    ByteBuffer bb = ByteBuffer.wrap(new byte[]{bite});
                    iterator.writeRelative(idx, bb);
                }
            } else {
                throw new IllegalArgumentException("Invalid depth specified:" + depth);
            }
        };

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

    /**
     * Get this tile as a 2d array.
     * The first index represents y, while the second represents x.
     * @return The 2d array.
     */
    public int[][] to2DArray(){
        int[][] array2d = new int[8][8];
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                int pxl = pixels[y * 8 + x];
                array2d[y][x] = pxl;
            }
        }
        return array2d;
    }

    /**
     * Get this tile as a 1d array.
     * @return The 1d array.
     */
    public int[] to1DArray(){
        return Arrays.copyOf(this.pixels, pixels.length);
    }

    @Override
    public String toString() {
        return "GBATile{" +
                "bitDepth=" + bitDepth +
                ", pixels=" + Arrays.toString(pixels) +
                '}';
    }
}
