package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.Bitmask;
import com.github.lucbui.bytes.ByteWindow;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.exception.HexerException;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.utility.HexUtils;
import com.github.lucbui.utility.MathUtils;

import java.io.Serializable;

/**
 * Metadata on a tile, for a Tilemap
 */
public class GBAMapTileMetadata implements Serializable {

    static final long serialVersionUID = 42L;

    private static Bitmask TILE_NUMBER_MASK = Bitmask.forBitRange(0, 9);
    private static Bitmask HORIZONTAL_FLIP_MASK = Bitmask.forBit(10);
    private static Bitmask VERTICAL_FLIP_MASK = Bitmask.forBit(11);
    private static Bitmask PALETTE_NUMBER_MASK = Bitmask.forBitRange(12, 15);

    public static final int HIGHEST_TILE_NUMBER = 1023;
    public static final int HIGHEST_PALETTE_NUMBER = 16;

    /**
     * Get a Hexer that reads a GBAMapTileMetadata
     */
    public static final Hexer<GBAMapTileMetadata> HEXER = new Hexer<GBAMapTileMetadata>() {
        @Override
        public int getSize(GBAMapTileMetadata object) {
            return 2;
        }

        @Override
        public GBAMapTileMetadata read(HexFieldIterator iterator) {
            ByteWindow bb = iterator.get(2).or(HexerException::new); iterator.advanceRelative(2);
            int val = HexUtils.byteToUnsignedByte(bb.get(0)) * 0x100 + HexUtils.byteToUnsignedByte(bb.get(1));
            short tileNumber = (short) MathUtils.assertInRange(TILE_NUMBER_MASK.apply(val), 0, HIGHEST_TILE_NUMBER);
            boolean horizontalFlip = HORIZONTAL_FLIP_MASK.apply(val) == 1;
            boolean verticalFlip = VERTICAL_FLIP_MASK.apply(val) == 1;
            byte paletteNumber = (byte)MathUtils.assertInRange(PALETTE_NUMBER_MASK.apply(val), 0, HIGHEST_PALETTE_NUMBER);
            return new GBAMapTileMetadata(tileNumber, horizontalFlip, verticalFlip, paletteNumber);
        }

        @Override
        public void write(GBAMapTileMetadata object, HexFieldIterator iterator) {
            int val = Bitmask.merge()
                    .with(TILE_NUMBER_MASK, object.getTileNumber())
                    .with(HORIZONTAL_FLIP_MASK, object.isHorizontalFlip() ? 1 : 0)
                    .with(VERTICAL_FLIP_MASK, object.isVerticalFlip() ? 1 : 0)
                    .with(PALETTE_NUMBER_MASK, object.getPaletteNumber())
                    .apply();
            iterator.write(HexUtils.toByteWindow(val, val >>> 8));
        }
    };

    private short tileNumber;
    private boolean horizontalFlip;
    private boolean verticalFlip;
    private byte paletteNumber;

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

        /**
         * Set the tile number this metadata references
         * @param tileNumber The tile number
         * @return This instance
         * @throws IllegalArgumentException tilenumber is not between 0 and HIGHEST_TILE_NUMBER
         */
        public Creator setTileNumber(int tileNumber){
            this.tileNumber = (short)MathUtils.assertInRange(tileNumber, 0, HIGHEST_TILE_NUMBER);
            return this;
        }

        /**
         * Set whether this tile is flipped horizontally.
         * @param horizontalFlip If true, tile is flipped horizontally.
         * @return This instance
         */
        public Creator setHorizontalFlip(boolean horizontalFlip){
            this.horizontalFlip = horizontalFlip;
            return this;
        }

        /**
         * Set whether this tile is flipped vertically.
         * @param verticalFlip If true, tile is flipped vertically.
         * @return This instance
         */
        public Creator setVerticalFlip(boolean verticalFlip){
            this.verticalFlip = verticalFlip;
            return this;
        }

        /**
         * Set the palette number for this tile.
         * For 256-bit tilemaps, this should ALWAYS be 0.
         * @param palette The palette to use.
         * @return This instance
         * @throws IllegalArgumentException Palette isn't between 0 and HIGHEST_PALETTE_NUMBER
         */
        public Creator setPalette(int palette){
            this.paletteNumber = (byte)MathUtils.assertInRange(tileNumber, 0, HIGHEST_PALETTE_NUMBER);
            return this;
        }

        /**
         * Creates a GBAMapTileMetadata
         * @return A newly-created map metadata.
         */
        public GBAMapTileMetadata create(){
            return new GBAMapTileMetadata(tileNumber, horizontalFlip, verticalFlip, paletteNumber);
        }
    }
}
