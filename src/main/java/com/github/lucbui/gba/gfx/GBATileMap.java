package com.github.lucbui.gba.gfx;

import com.github.lucbui.gba.exception.IllegalSizeException;
import com.sun.xml.internal.ws.util.StreamUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the combination of Tiles and their metadata to create a full image.
 */
public class GBATileMap implements GBAGraphic, Serializable {

    static final long serialVersionUID = 42L;

    /**
     * The width of a tilemap, in tiles.
     */
    public static final int MAP_WIDTH = 32;
    /**
     * The height of a tilemap, in tiles.
     */
    public static final int MAP_HEIGHT = 32;
    /**
     * The number of tiles in a tilemap.
     */
    public static final int MAP_AREA = MAP_WIDTH * MAP_HEIGHT;

    private final GBATile[] tiles;
    private final GBAMapTileMetadata[] map;

    /**
     * Creates a GBATileMap from an assortment of tiles and their corresponding map.
     * @param tiles A list of tiles.
     * @param map A list of map Metadata pieces, one for each tile on the map.
     */
    private GBATileMap(GBATile[] tiles, GBAMapTileMetadata[] map){
        this.tiles = tiles;
        this.map = map;
    }

    /**
     * Get the tile at the specified slot.
     * @param slot The slot of the tile.
     * @return
     */
    public GBATile getTile(int slot){
        return tiles[slot];
    }

    /**
     * Get the metadata for the specified slot.
     * @param slot The slot of the tile.
     * @return
     */
    public GBAMapTileMetadata getMapTileMetadata(int slot){
        return map[slot];
    }

    @Override
    public int[] to1DArray() {
        int[] array = new int[getWidth() * getHeight()];
        for(int mapTile = 0; mapTile < map.length; mapTile++){
            int tileX = mapTile % MAP_WIDTH;
            int tileY = mapTile / MAP_WIDTH;
            GBAMapTileMetadata metadata = map[mapTile];
            if(tiles.length >= metadata.getTileNumber()){
                throw new ArrayIndexOutOfBoundsException("Tile " + metadata.getTileNumber() + " specified, with only " + (tiles.length - 1) + "tiles available");
            }
            GBATile tile = tiles[metadata.getTileNumber()];
            for(int y = 0; y < GBATile.HEIGHT_IN_PIXELS; y++){
                int[] row = getRowForMetadata(tile, metadata, y);
                //Convert from tilespace to pixelspace.
                //(y * width) + x
                System.arraycopy(row, 0, array, (tileY * GBATile.HEIGHT_IN_PIXELS + y) * (MAP_WIDTH * GBATile.WIDTH_IN_PIXELS) + (tileX * GBATile.WIDTH_IN_PIXELS) , row.length);
            }
        }
        return array;
    }

    /**
     * Gets a row from a tile, applying the relevant flipping and palette swapping.
     * @param tile The tile to retrieve from.
     * @param metadata The metadata to use during retrieval
     * @param y The Y position to read from (irrespective of the flip).
     * @return The specified row.
     */
    protected int[] getRowForMetadata(GBATile tile, GBAMapTileMetadata metadata, int y) {
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

    /**
     * Modify this GBATileMap
     * This returns a Creator which modifies the current tilemap. The tilemap returned from the creator is an all-new
     * tilemap based on this one.
     * @return A creator to modify this tilemap.
     */
    public Creator modify(){
        return new Creator(this);
    }

    /**
     * Create a GBATileMap
     * @param bitDepth The BitDepth to use for this tilemap.
     * @return A creator to create a tilemap.
     */
    public static Creator create(BitDepth bitDepth){
        Objects.requireNonNull(bitDepth);
        return new Creator(bitDepth);
    }

    /**
     * Creator class for creating Tilemaps.
     */
    public static class Creator {
        private List<GBATile.Creator> tileCreators;
        private List<GBAMapTileMetadata.Creator> mapCreators;
        //The highest tile set in this creator. When creating, the list is truncated to between 0 and highest,
        //before being created and returned.
        private int highestTileAdded;
        private BitDepth bitDepth;

        private Creator(GBATileMap tileMap){
            this.tileCreators = Arrays.stream(tileMap.tiles).map(GBATile::modify).collect(Collectors.toList());
            //Pad out the tileMap's list to the highest number possible. We'll shrink this if we need to later.
            Stream.iterate(tileMap.tiles.length, i -> i+1)
                    .limit(GBAMapTileMetadata.HIGHEST_TILE_NUMBER)
                    .forEach(i -> tileCreators.add(GBATile.build(tileMap.tiles[0].getBitDepth())));
            this.mapCreators = Arrays.stream(tileMap.map).map(GBAMapTileMetadata::modify).collect(Collectors.toList());
            highestTileAdded = tileMap.tiles.length;
            this.bitDepth = tileMap.tiles[0].getBitDepth();
        }

        private Creator(BitDepth bitDepth){
            this.tileCreators = new ArrayList<>();
            //Pad out the tileMap's list to the highest number possible. We'll shrink this if we need to later.
            Stream.iterate(0, i -> i+1)
                    .limit(GBAMapTileMetadata.HIGHEST_TILE_NUMBER)
                    .forEach(i -> tileCreators.add(GBATile.build(bitDepth)));
            this.mapCreators = new ArrayList<>();
            //Pad out the map's metadata to the correct value.
            Stream.iterate(0, i -> i+1)
                    .limit(MAP_AREA)
                    .forEach(i -> mapCreators.add(GBAMapTileMetadata.build()));
            //We set this to 1, because in the very worst case, we return just an array of the first blank tile.
            highestTileAdded = 1;
            this.bitDepth = bitDepth;
        }

        private void verifyTileBounds(int tileNum){
            if(tileNum < 0 || tileNum >= tileCreators.size()){
                throw new IndexOutOfBoundsException("tile slot must be between 0 and " + tileCreators.size());
            }
        }

        private void verifyMapBounds(int tileNum){
            if(tileNum < 0 || tileNum >= mapCreators.size()){
                throw new IndexOutOfBoundsException("map slot must be between 0 and " + mapCreators.size());
            }
        }

        /**
         * Set the x/y of the specified tile to a pixel value.
         * @param tileNum The tile to use.
         * @param x The X to modify.
         * @param y The Y to modify.
         * @param pixel The pixel to set to.
         * @return This instance.
         * @throws IndexOutOfBoundsException tileNum, x, or y are not in bounds, or the pixel is not valid
         * for the provided bitDepth.
         */
        public Creator setPixel(int tileNum, int x, int y, int pixel){
            verifyTileBounds(tileNum);
            bitDepth.verifyPixel(pixel);
            tileCreators.get(tileNum).setPixel(x, y, pixel);
            highestTileAdded = Math.max(highestTileAdded, tileNum);
            return this;
        }

        /**
         * Set a tile slot to a specified tile.
         * @param tileNum The tile slot to set.
         * @param tile The tile to replace with.
         * @return This instance
         * @throws IndexOutOfBoundsException tileNum is not in bounds
         * @throws NullPointerException tile is null.
         */
        public Creator setTile(int tileNum, GBATile tile){
            verifyTileBounds(tileNum);
            Objects.requireNonNull(tile);
            tileCreators.set(tileNum, tile.modify());
            return this;
        }

        /**
         * Set the tile number of a metadata slot.
         * @param slot The slot to retrieve.
         * @param tileNum The tile number to change to.
         * @return This instance
         * @throws IndexOutOfBoundsException slot or tileNum is not in bounds.
         */
        public Creator setTileNumber(int slot, int tileNum){
            verifyMapBounds(slot);
            mapCreators.get(slot).setTileNumber(tileNum);
            return this;
        }

        /**
         * Set the palette number of a metadata slot.
         * @param slot The slot to retrieve.
         * @param paletteNum The palette number to change to.
         * @return This instance
         * @throws IndexOutOfBoundsException slot or paletteNum is not in bounds.
         */
        public Creator setPaletteNumber(int slot, int paletteNum){
            verifyMapBounds(slot);
            mapCreators.get(slot).setPalette(paletteNum);
            return this;
        }

        /**
         * Set the horizontal flip of a metadata slot.
         * @param slot The slot to retrieve.
         * @param flip If true, the tile is flipped horizontally.
         * @return This instance
         * @throws IndexOutOfBoundsException slot is not in bounds.
         */
        public Creator setHorizontalFlip(int slot, boolean flip){
            verifyMapBounds(slot);
            mapCreators.get(slot).setHorizontalFlip(flip);
            return this;
        }

        /**
         * Set the vertical flip of a metadata slot.
         * @param slot The slot to retrieve.
         * @param flip If true, the tile is flipped vertically.
         * @return This instance
         * @throws IndexOutOfBoundsException slot is not in bounds.
         */
        public Creator setVerticalFlip(int slot, boolean flip){
            verifyMapBounds(slot);
            mapCreators.get(slot).setVerticalFlip(flip);
            return this;
        }

        /**
         * Set a metadata slot to the specified metadata.
         * @param slot The slot to modify
         * @param metadata The metadata to set the slot to.
         * @return This instance.
         */
        public Creator setMapTileMetadata(int slot, GBAMapTileMetadata metadata){
            verifyMapBounds(slot);
            mapCreators.set(slot, metadata.modify());
            return this;
        }

        /**
         * Create the tilemap.
         * @return The newly-created tilemap
         */
        public GBATileMap create(){
            return new GBATileMap(
                    tileCreators
                            //Shrink the tiles to only include tiles we've manipulated.
                            .subList(0, highestTileAdded)
                            .stream()
                            .map(GBATile.Creator::create).toArray(GBATile[]::new),
                    mapCreators.stream()
                            .map(GBAMapTileMetadata.Creator::create).toArray(GBAMapTileMetadata[]::new)
            );
        }
    }
}
