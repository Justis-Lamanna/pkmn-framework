package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class GBASprite implements GBAGraphic, Serializable {

    static final long serialVersionUID = 42L;

    private GBATile[] tiles;
    private BitDepth bitDepth;
    private SpriteSize spriteSize;

    private static void verifyBounds(int x, int y, SpriteSize spriteSize){
        if(x < 0 || x >= spriteSize.getWidthInPixels()){
            throw new IndexOutOfBoundsException("x must be between 0 and " + spriteSize.getWidthInPixels());
        }
        if(y < 0 || y >= spriteSize.getHeightInPixels()){
            throw new IndexOutOfBoundsException("y must be between 0 and " + spriteSize.getHeightInPixels());
        }
    }

    /**
     * Create a GBASprite from a list of GBATiles.
     * @param tiles The tiles to use.
     * @param spriteSize The sprite size to use.
     * @throws IllegalArgumentException TileWidth or TileHeight are 0, or tileWidth * tileHeight does not equal
     * the number of tiles, or a mixture of bit depths are provided in the tiles.
     */
    private GBASprite(GBATile[] tiles, SpriteSize spriteSize){
        this.tiles = tiles;
        this.bitDepth = tiles[0].getBitDepth();
        this.spriteSize = spriteSize;
    }

    /**
     * Get the SpriteSize of this graphic.
     * @return
     */
    public SpriteSize getSpriteSize(){
        return spriteSize;
    }

    /**
     * Get the tile at a given slot.
     * @param slot The slot to retrieve.
     * @return
     */
    public GBATile getTile(int slot){
        return tiles[slot];
    }

    /**
     * Get the bit depth of this sprite.
     * @return
     */
    public BitDepth getBitDepth(){
        return bitDepth;
    }

    /**
     * Get the width of this sprite, in tiles.
     * @return
     */
    public int getTileWidth(){
        return getSpriteSize().getWidth();
    }

    /**
     * Get the height of this sprite, in tiles.
     * @return
     */
    public int getTileHeight(){
        return getSpriteSize().getHeight();
    }

    @Override
    public int getWidth(){
        return getTileWidth() * GBATile.WIDTH_IN_PIXELS;
    }

    @Override
    public int getHeight(){
        return getTileHeight() * GBATile.HEIGHT_IN_PIXELS;
    }

    @Override
    public int[] to1DArray() {
        int[] pixels = new int[tiles.length * GBATile.AREA_IN_PIXELS];
        for(int tileY = 0; tileY < getTileHeight(); tileY++){
            for(int tileX = 0; tileX < getTileWidth(); tileX++){
                GBATile tile = tiles[tileY * getTileWidth() + tileX];
                for(int y = 0; y < GBATile.HEIGHT_IN_PIXELS; y++){
                    int[] row = tile.getRow(y);
                    //Convert from tilespace to pixelspace.
                    //(y * width) + x
                    System.arraycopy(row, 0, pixels, (tileY * GBATile.HEIGHT_IN_PIXELS + y) * (getTileWidth() * GBATile.WIDTH_IN_PIXELS) + (tileX * GBATile.WIDTH_IN_PIXELS), row.length);
                }
            }
        }
        return pixels;
    }

    @Override
    public Type getType() {
        return Type.INDEXED;
    }

    /**
     * Modify this GBASprite.
     * This returns a Creator which modifies the current sprite. The sprite returned from the creator is an all-new
     * sprite built from this one.
     * @return A creator to modify this sprite.
     */
    public Creator modify(){
        return new Creator(this);
    }

    /**
     * Create a GBASprite.
     * Returns a Creator for creating a sprite at the specified bitDepth and spriteSize.
     * @param bitDepth The bit depth to create the image at.
     * @param spriteSize The sprite size to use.
     * @return A creator to create a Sprite.
     * @throws NullPointerException bitDepth or spriteSize is null.
     */
    public static Creator create(BitDepth bitDepth, SpriteSize spriteSize){
        Objects.requireNonNull(bitDepth);
        Objects.requireNonNull(spriteSize);
        return new Creator(bitDepth, spriteSize);
    }

    /**
     * Get a hex reader to read a GBASprite
     * @param bitDepth The bit depth to read as.
     * @param spriteSize The sprite size to use.
     * @return A hex reader that can read the specified type of object.
     * @throws NullPointerException bitDepth or spriteSize is null.
     */
    public static HexReader<GBASprite> getHexReader(BitDepth bitDepth, SpriteSize spriteSize){
        Objects.requireNonNull(bitDepth);
        Objects.requireNonNull(spriteSize);
        return iterator -> {
            int numberOfTiles = spriteSize.getArea();
            GBATile[] tiles = new GBATile[numberOfTiles];
            for(int idx = 0; idx < numberOfTiles; idx++){
                GBATile tile = GBATile.getHexReader(bitDepth).read(iterator);
                tiles[idx] = tile;
            }
            return new GBASprite(tiles, spriteSize);
        };
    }

    /**
     * Get a hex writer to write a GBASprite
     * @param bitDepth The bit depth to write as.
     * @param spriteSize The sprite size to use.
     * @return A hex writer that can write the specified type of object.
     * @throws NullPointerException bitDepth or spriteSize is null.
     */
    public static HexWriter<GBASprite> getHexWriter(BitDepth bitDepth, SpriteSize spriteSize){
        Objects.requireNonNull(bitDepth);
        Objects.requireNonNull(spriteSize);
        return (object, iterator) -> {
            int numberOfTiles = spriteSize.getArea();
            for(int idx = 0; idx < numberOfTiles; idx++){
                GBATile.getHexWriter(bitDepth).write(object.getTile(idx), iterator);
            }
        };
    }

    /**
     * A creator for making GBASprites.
     */
    public static class Creator{
        private GBATile.Creator[] tiles;
        private BitDepth bitDepth;
        private SpriteSize spriteSize;

        private Creator(BitDepth bitDepth, SpriteSize spriteSize){
            this.bitDepth = bitDepth;
            this.spriteSize = spriteSize;
            this.tiles = new GBATile.Creator[spriteSize.getArea()];
            Arrays.setAll(tiles, i -> GBATile.build(bitDepth));
        }

        private Creator(GBASprite sprite){
            this.bitDepth = sprite.bitDepth;
            this.spriteSize = sprite.spriteSize;
            this.tiles = new GBATile.Creator[spriteSize.getArea()];
            Arrays.setAll(tiles, i -> sprite.tiles[i].modify());
        }

        /**
         * Set the pixel at the specified value.
         * @param x The x position in the sprite.
         * @param y The y position in the sprite.
         * @param pixel The pixel to set it to.
         * @return This instance.
         * @throws IllegalArgumentException x or y exceed the sprite's boundaries, or the pixel value
         * exceeds the maxium for the bitDepth specified.
         */
        public Creator setPixel(int x, int y, int pixel){
            verifyBounds(x, y, spriteSize);
            bitDepth.verifyPixel(pixel);
            //First, get tile
            int tileX = x / GBATile.WIDTH_IN_PIXELS;
            int tileY = y / GBATile.HEIGHT_IN_PIXELS;
            //Then, relative position inside tile
            int pixelInTileX = x % GBATile.WIDTH_IN_PIXELS;
            int pixelInTileY = y % GBATile.HEIGHT_IN_PIXELS;
            this.tiles[tileY * spriteSize.getWidth() + tileX].setPixel(pixelInTileX, pixelInTileY, pixel);
            return this;
        }

        /**
         * Create a GBASprite
         * @return A new GBASprite
         */
        public GBASprite create(){
           GBATile[] builtTiles = Arrays.stream(tiles).map(GBATile.Creator::create).toArray(GBATile[]::new);
           return new GBASprite(builtTiles, spriteSize);
        }
    }
}
