package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.file.HexFieldIterator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GBASprite implements GBAGraphic{
    private GBATile[] tiles;
    private BitDepth bitDepth;
    private int tileWidth;
    private int tileHeight;

    /**
     * Create a blank GBASprite, filled with blank tiles.
     * @param bitDepth The Bit depth of the sprite
     * @param tileWidth The width of the sprite, in tiles.
     * @param tileHeight The height of the sprite, in tiles.
     */
    public GBASprite(BitDepth bitDepth, int tileWidth, int tileHeight){
        Objects.requireNonNull(bitDepth);
        if(tileWidth <= 0 || tileHeight <= 0){
            throw new IllegalArgumentException("tileWidth and tileHeight must be greater than 0");
        }
        this.tiles = new GBATile[tileWidth * tileHeight];
        Arrays.setAll(this.tiles, i -> new GBATile(bitDepth));
        this.bitDepth = bitDepth;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * Create a GBASprite from a list of GBATiles.
     * @param tiles The tiles to use.
     * @param tileWidth The width, in tiles.
     * @param tileHeight The height, in tiles.
     * @throws IllegalArgumentException TileWidth or TileHeight are 0, or tileWidth * tileHeight does not equal
     * the number of tiles, or a mixture of bit depths are provided in the tiles.
     */
    public GBASprite(GBATile[] tiles, int tileWidth, int tileHeight){
        Objects.requireNonNull(tiles);
        if(tileWidth <= 0 || tileHeight <= 0){
            throw new IllegalArgumentException("tileWidth and tileHeight must be less than 0");
        }
        if(tiles.length != tileWidth * tileHeight){
            throw new IllegalArgumentException("tiles must be equal to tileWidth * tileHeight");
        }
        List<BitDepth> bitDepths = Arrays.stream(tiles).map(GBATile::getBitDepth).distinct().collect(Collectors.toList());
        if(bitDepths.size() > 1){
            throw new IllegalArgumentException("Mixture of BitDepths provided. All tiles must be uniform bit depth");
        }
        this.tiles = Arrays.copyOf(tiles, tiles.length);
        this.bitDepth = bitDepths.get(0);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
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
        return tileWidth;
    }

    /**
     * Get the height of this sprite, in tiles.
     * @return
     */
    public int getTileHeight(){
        return tileHeight;
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
        for(int tileY = 0; tileY < tileHeight; tileY++){
            for(int tileX = 0; tileX < tileWidth; tileX++){
                GBATile tile = tiles[tileY * tileWidth + tileX];
                for(int y = 0; y < 8; y++){
                    int[] row = tile.getRowAt(y);
                    //Convert from tilespace to pixelspace.
                    System.arraycopy(row, 0, pixels, (tileY * GBATile.HEIGHT_IN_PIXELS + y) * (tileWidth * GBATile.WIDTH_IN_PIXELS) + (tileX * GBATile.WIDTH_IN_PIXELS), row.length);
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
     * Get a hex reader to read a GBASprite
     * @param bitDepth The bit depth to read as.
     * @param tileWidth The width, in tiles.
     * @param tileHeight The height, in tiles.
     * @return A hex reader that can read the specified type of object.
     */
    public static HexReader<GBASprite> getHexReader(BitDepth bitDepth, int tileWidth, int tileHeight){
        return iterator -> {
            int numberOfTiles = tileWidth * tileHeight;
            GBATile[] tiles = new GBATile[numberOfTiles];
            for(int idx = 0; idx < numberOfTiles; idx++){
                GBATile tile = GBATile.getHexReader(bitDepth).read(iterator);
                tiles[idx] = tile;
            }
            return new GBASprite(tiles, tileWidth, tileHeight);
        };
    }

    /**
     * Get a hex writer to write a GBASprite
     * @param bitDepth The bit depth to write as.
     * @param tileWidth The width, in tiles.
     * @param tileHeight The height, in tiles.
     * @return A hex writer that can write the specified type of object.
     */
    public static HexWriter<GBASprite> getHexWriter(BitDepth bitDepth, int tileWidth, int tileHeight){
        return (object, iterator) -> {
            int numberOfTiles = tileWidth * tileHeight;
            for(int idx = 0; idx < numberOfTiles; idx++){
                GBATile.getHexWriter(bitDepth).write(object.getTile(idx), iterator);
            }
        };
    }
}
