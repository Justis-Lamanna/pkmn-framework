package com.github.lucbui.gba.gfx;

import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents the combination of Tiles and their metadata to create a full image.
 */
public class GBATileMap implements GBAGraphic, Serializable {

    static final long serialVersionUID = 42L;

    public static final int MAP_WIDTH = 32;
    public static final int MAP_HEIGHT = 32;

    private final List<GBATile> tiles;
    private final List<GBAMapTileMetadata> map;

    /**
     * Creates a GBATileMap from an assortment of tiles and their corresponding map.
     * @param tiles A list of tiles.
     * @param map A list of map Metadata pieces, one for each tile on the map.
     */
    public GBATileMap(List<GBATile> tiles, List<GBAMapTileMetadata> map){
        Objects.requireNonNull(tiles);
        Objects.requireNonNull(map);
        if(map.size() != MAP_WIDTH * MAP_HEIGHT){
            throw new IllegalArgumentException("Map should be " + (MAP_WIDTH * MAP_HEIGHT) + " entries long.");
        }
        this.tiles = tiles;
        this.map = map;
    }

    /**
     * Get the tile at the specified slot.
     * @param slot The slot of the tile.
     * @return
     */
    public GBATile getTile(int slot){
        return tiles.get(slot);
    }

    /**
     * Get the metadata for the specified slot.
     * @param slot The slot of the tile.
     * @return
     */
    public GBAMapTileMetadata getMapTileMetadata(int slot){
        return map.get(slot);
    }

    @Override
    public int[] to1DArray() {
        int[] array = new int[getWidth() * getHeight()];
        for(int mapTile = 0; mapTile < map.size(); mapTile++){
            int tileX = mapTile % MAP_WIDTH;
            int tileY = mapTile / MAP_WIDTH;
            GBAMapTileMetadata metadata = map.get(mapTile);
            if(tiles.size() >= metadata.getTileNumber()){
                throw new ArrayIndexOutOfBoundsException("Tile " + metadata.getTileNumber() + " specified, with only " + (tiles.size() - 1) + "tiles available");
            }
            GBATile tile = tiles.get(metadata.getTileNumber());
            for(int y = 0; y < GBATile.HEIGHT_IN_PIXELS; y++){
                int[] row = getRowForMetadata(tile, metadata, y);
                //Convert from tilespace to pixelspace.
                //(y * width) + x
                System.arraycopy(row, 0, array, (tileY * GBATile.HEIGHT_IN_PIXELS + y) * (MAP_WIDTH * GBATile.WIDTH_IN_PIXELS) + (tileX * GBATile.WIDTH_IN_PIXELS) , row.length);
            }
        }
        return new int[0];
    }

    /**
     * Gets a row from a tile, applying the relevant flipping and palette swapping.
     * @param tile The tile to retrieve from.
     * @param metadata The metadata to use during retrieval
     * @param y The Y position to read from (irrespective of the flip).
     * @return The specified row.
     */
    private int[] getRowForMetadata(GBATile tile, GBAMapTileMetadata metadata, int y) {
        int[] row;
        if(metadata.isVerticalFlip()){
            row = tile.getRow((GBATile.HEIGHT_IN_PIXELS - 1) - y);
        } else {
            row = tile.getRow(y);
        }
        if(metadata.isHorizontalFlip()){
            ArrayUtils.reverse(row);
        }
        //Shift the pixels by the 16 * palette number
        row = Arrays.stream(row).map(pixel -> pixel + (16 * metadata.getPaletteNumber())).toArray();
        return row;
    }

    @Override
    public Type getType() {
        return Type.INDEXED;
    }

    @Override
    public int getWidth() {
        return MAP_WIDTH * GBATile.WIDTH_IN_PIXELS;
    }

    @Override
    public int getHeight() {
        return MAP_HEIGHT * GBATile.HEIGHT_IN_PIXELS;
    }
}
