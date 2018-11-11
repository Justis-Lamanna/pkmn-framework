package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.*;
import com.github.lucbui.file.HexFieldIterator;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Metadata on a tile, for a Tilemap
 */
public class GBAMapTileMetadata implements Serializable {

    static final long serialVersionUID = 42L;

    private static Bitmask TILE_NUMBER_MASK = Bitmask.forBitRange(0, 9);
    private static Bitmask HORIZONTAL_FLIP_MASK = Bitmask.forBit(10);
    private static Bitmask VERTICAL_FLIP_MASK = Bitmask.forBit(11);
    private static Bitmask PALETTE_NUMBER_MASK = Bitmask.forBitRange(12, 15);

    private static final int HIGHEST_TILE_NUMBER = 1023;
    private static final int HIGHEST_PALETTE_NUMBER = 16;

    private short tileNumber;
    private boolean horizontalFlip;
    private boolean verticalFlip;
    private byte paletteNumber;

    private static int verifyInRange(String varName, int number, int lowBound, int highBound){
        if(number < lowBound || number > highBound){
            throw new IllegalArgumentException(varName + " exceeds bounds, must be between " + lowBound + " and " + highBound);
        }
        return number;
    }

    /**
     * Constructs a GBAMapTileMetadata.
     * @param tileNumber The tile number to use.
     * @param horizontalFlip If true, this tile is flipped horizontally.
     * @param verticalFlip If true, this tile is flipped vertically.
     * @param paletteNumber The palette to use, if 16 palette mode.
     */
    private GBAMapTileMetadata(short tileNumber, boolean horizontalFlip, boolean verticalFlip, byte paletteNumber) {
        this.tileNumber = tileNumber;
        this.horizontalFlip = horizontalFlip;
        this.verticalFlip = verticalFlip;
        this.paletteNumber = paletteNumber;
    }

    /**
     * Get the tile number.
     * @return
     */
    public int getTileNumber() {
        return tileNumber;
    }

    /**
     * Check if flipped horizontally.
     * @return
     */
    public boolean isHorizontalFlip() {
        return horizontalFlip;
    }

    /**
     * Check if flipped vertically.
     * @return
     */
    public boolean isVerticalFlip() {
        return verticalFlip;
    }

    /**
     * Get the palette to use for this tile.
     * @return
     */
    public int getPaletteNumber() {
        return paletteNumber;
    }

    @Override
    public String toString() {
        return "GBAMapTileMetadata{" +
                "tileNumber=" + tileNumber +
                ", horizontalFlip=" + horizontalFlip +
                ", verticalFlip=" + verticalFlip +
                ", paletteNumber=" + paletteNumber +
                '}';
    }

    /**
     * Modify this GBAMapTileMetadata
     * @return A creator, populated with this GBAMapTileMetadata data.
     */
    public Creator modify(){
        return new Creator(this);
    }

    /**
     * Create a GBAMapTileMetadata
     * @return A creator, populated with empty default metadata.
     */
    public static Creator build(){
        return new Creator();
    }

    /**
     * Get a Hex Reader that reads a GBAMapTileMetadata
     * @return
     */
    public static HexReader<GBAMapTileMetadata> getHexReader(){
        return iterator -> {
            ByteBuffer bb = iterator.get(2); iterator.advanceRelative(2);
            int val = HexUtils.fromByteBufferToInt(bb);
            short tileNumber = (short)verifyInRange("tileNumber", TILE_NUMBER_MASK.apply(val), 0, HIGHEST_TILE_NUMBER);
            boolean horizontalFlip = HORIZONTAL_FLIP_MASK.apply(val) == 1;
            boolean verticalFlip = VERTICAL_FLIP_MASK.apply(val) == 1;
            byte paletteNumber = (byte)verifyInRange("paletteNumber", PALETTE_NUMBER_MASK.apply(val), 0, HIGHEST_PALETTE_NUMBER);
            return new GBAMapTileMetadata(tileNumber, horizontalFlip, verticalFlip, paletteNumber);
        };
    }

    /**
     * Get a Hex Writer that writes a GBAMapTileMetadata
     * @return
     */
    public static HexWriter<GBAMapTileMetadata> getHexWriter(){
        return (object, iterator) -> {
            int val = Bitmask.merge()
                    .with(TILE_NUMBER_MASK, object.getTileNumber())
                    .with(HORIZONTAL_FLIP_MASK, object.isHorizontalFlip() ? 1 : 0)
                    .with(VERTICAL_FLIP_MASK, object.isVerticalFlip() ? 1 : 0)
                    .with(PALETTE_NUMBER_MASK, object.getPaletteNumber())
                    .apply();
            iterator.write(HexUtils.toByteBuffer(val, val >>> 8));
        };
    }

    public static class Creator{
        private short tileNumber;
        private boolean horizontalFlip;
        private boolean verticalFlip;
        private byte paletteNumber;

        private Creator(GBAMapTileMetadata metadata){
            tileNumber = metadata.tileNumber;
            horizontalFlip = metadata.horizontalFlip;
            verticalFlip = metadata.verticalFlip;
            paletteNumber = metadata.paletteNumber;
        }

        private Creator(){
            tileNumber = 0;
            horizontalFlip = false;
            verticalFlip = false;
            paletteNumber = 0;
        }

        public Creator setTileNumber(int tileNumber){
            this.tileNumber = (short)verifyInRange("tileNumber", tileNumber, 0, HIGHEST_TILE_NUMBER);
            return this;
        }

        public Creator setHorizontalFlip(boolean horizontalFlip){
            this.horizontalFlip = horizontalFlip;
            return this;
        }

        public Creator setVerticalFlip(boolean verticalFlip){
            this.verticalFlip = verticalFlip;
            return this;
        }

        public Creator setPalette(int palette){
            this.paletteNumber = (byte)verifyInRange("palette", tileNumber, 0, HIGHEST_PALETTE_NUMBER);
            return this;
        }

        public GBAMapTileMetadata create(){
            return new GBAMapTileMetadata(tileNumber, horizontalFlip, verticalFlip, paletteNumber);
        }
    }
}
