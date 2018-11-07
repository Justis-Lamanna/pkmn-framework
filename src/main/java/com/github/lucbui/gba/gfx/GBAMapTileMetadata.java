package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.*;
import com.github.lucbui.file.HexFieldIterator;

import java.nio.ByteBuffer;

/**
 * Metadata on a tile, for a Tilemap
 */
public class GBAMapTileMetadata {

    private static Bitmask TILE_NUMBER_MASK = Bitmask.forBitRange(0, 9);
    private static Bitmask HORIZONTAL_FLIP_MASK = Bitmask.forBit(10);
    private static Bitmask VERTICAL_FLIP_MASK = Bitmask.forBit(11);
    private static Bitmask PALETTE_NUMBER_MASK = Bitmask.forBitRange(12, 15);

    private int tileNumber;
    private boolean horizontalFlip;
    private boolean verticalFlip;
    private int paletteNumber;

    /**
     * Constructs a GBAMapTileMetadata.
     * @param tileNumber The tile number to use.
     * @param horizontalFlip If true, this tile is flipped horizontally.
     * @param verticalFlip If true, this tile is flipped vertically.
     * @param paletteNumber The palette to use, if 16 palette mode.
     */
    public GBAMapTileMetadata(int tileNumber, boolean horizontalFlip, boolean verticalFlip, int paletteNumber) {
        this.tileNumber = tileNumber;
        this.horizontalFlip = horizontalFlip;
        this.verticalFlip = verticalFlip;
        this.paletteNumber = paletteNumber;
    }

    /**
     * Constructs a GBAMapTileMetadata, for 256-color mode.
     * @param tileNumber The tile number to use.
     * @param horizontalFlip If true, this tile is flipped horizontally.
     * @param verticalFlip If true, this tile is flipped vertically.
     */
    public GBAMapTileMetadata(int tileNumber, boolean horizontalFlip, boolean verticalFlip) {
        this.tileNumber = tileNumber;
        this.horizontalFlip = horizontalFlip;
        this.verticalFlip = verticalFlip;
        this.paletteNumber = 0;
    }

    /**
     * Constructs a GBAMapTileMetadata with no flipping.
     * @param tileNumber The tile number to use.
     * @param paletteNumber The palette to use, if 16 palette mode.
     */
    public GBAMapTileMetadata(int tileNumber, int paletteNumber) {
        this.tileNumber = tileNumber;
        this.paletteNumber = paletteNumber;
        this.horizontalFlip = this.verticalFlip = false;
    }

    /**
     * Constructs a GBAMapTileMetadata with no flipping, for 256-color mode.
     * @param tileNumber The tile number to use.
     */
    public GBAMapTileMetadata(int tileNumber) {
        this.tileNumber = tileNumber;
        this.paletteNumber = 0;
        this.horizontalFlip = this.verticalFlip = false;
    }

    /**
     * An empty GBAMapTileMetadata.
     * By default, uses tile 0, palette 0, and has no flipping.
     */
    public GBAMapTileMetadata() {
        this.tileNumber = 0;
        this.paletteNumber = 0;
        this.horizontalFlip = this.verticalFlip = false;
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
     * Get a Hex Reader that reads a GBAMapTileMetadata
     * @return
     */
    public static HexReader<GBAMapTileMetadata> getHexReader(){
        return iterator -> {
            ByteBuffer bb = iterator.get(2); iterator.advanceRelative(2);
            int val = HexUtils.fromByteBufferToInt(bb);
            int tileNumber = TILE_NUMBER_MASK.apply(val);
            boolean horizontalFlip = HORIZONTAL_FLIP_MASK.apply(val) == 1;
            boolean verticalFlip = VERTICAL_FLIP_MASK.apply(val) == 1;
            int paletteNumber = PALETTE_NUMBER_MASK.apply(val);
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
}
