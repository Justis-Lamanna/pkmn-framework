package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.file.HexFieldIterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GBATiles {

    private BitDepth bitDepth;

    private GBATile[] tiles;

    /**
     * Create a collection of tiles.
     * @param bitDepth The BitDepth of this map.
     * @param tiles The tiles to use.
     */
    public GBATiles(BitDepth bitDepth, GBATile[] tiles){
        Objects.requireNonNull(bitDepth);
        Objects.requireNonNull(tiles);
        this.bitDepth = bitDepth;
        this.tiles = Arrays.copyOf(tiles, tiles.length);
    }

    /**
     * Create a collection of tiles from a 1D array.
     * The first 64 pixels are read into the first tile, the second 64 into the second tile,
     * and so on.
     * @param bitDepth The BitDepth of this map.
     * @param pixels The pixels to use.
     */
    public GBATiles(BitDepth bitDepth, int[] pixels){
        Objects.requireNonNull(bitDepth);
        Objects.requireNonNull(pixels);
        this.bitDepth = bitDepth;
        GBATile[] tiles = new GBATile[pixels.length / 64];
        for(int idx = 0; idx < tiles.length; idx++){
            tiles[idx] = new GBATile(bitDepth, Arrays.copyOfRange(pixels, idx * 64, (idx + 1) * 64));
        }
        this.tiles = tiles;
    }

    /**
     * Create a collection of tiles from a 2D array.
     * The 8x8 pixel block at the (0, 0) is the first tile, the one at (0, 1) is the second tile,
     * and so on.
     * @param bitDepth The BitDepth of this map.
     * @param pixels The pixels to use.
     */
    public GBATiles(BitDepth bitDepth, int[][] pixels){
        Objects.requireNonNull(bitDepth);
        Objects.requireNonNull(pixels);
        int height = pixels.length;
        int width = pixels[0].length;
        int heightInTiles = height >>> 3;
        int widthInTiles = width >>> 3;
        GBATile[] tiles = new GBATile[heightInTiles * widthInTiles];
        Arrays.setAll(tiles, i -> new GBATile(bitDepth));
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixel = pixels[y][x];
                tiles[(y / 8) * widthInTiles + (x / 8)]
                        .setPixelAt(x & 0b111, y & 0b111, pixel);
            }
        }
        this.tiles = tiles;
    }

    /**
     * Get the tile at the specified offset.
     * @param idx The index of the tile.
     * @return
     */
    public GBATile getTile(int idx){
        return tiles[idx];
    }

    /**
     * Set the tile at the specified offset.
     * @param idx The index of the tile.
     * @param tile The tile to replace with.
     */
    public void setTile(int idx, GBATile tile){
        tiles[idx] = tile;
    }

    /**
     * Get the BitDepth of this collection
     * @return The tiles.
     */
    public BitDepth getBitDepth(){
        return bitDepth;
    }

    @Override
    public String toString() {
        return "GBATiles{" +
                "bitDepth=" + bitDepth +
                ", tiles=" + Arrays.toString(tiles) +
                '}';
    }

    /**
     * Converts the GBATiles to a text-based image.
     * @param widthInTiles The width of the image, in tiles.
     * @param heightInTiles The height of the image, in tiles.
     * @return A string that "draws" the tiles.
     */
    public String toImageyString(int widthInTiles, int heightInTiles){
        return Arrays.stream(to2DArray(widthInTiles, heightInTiles))
                .map(i -> Arrays.stream(i).mapToObj(Integer::toHexString).toArray(String[]::new))
                .map(i ->String.join(" ", i))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Converts the GBATiles to a 2-dimensional pixel array.
     * All tiles are positioned, spatially, as if the tiles were arranged with the
     * specified width/height combination.
     * @param widthInTiles The width of the image, in tiles.
     * @param heightInTiles The height of the image, in tiles.
     * @return A 2D array describing the tiles.
     */
    public int[][] to2DArray(int widthInTiles, int heightInTiles){
        if(widthInTiles * heightInTiles != tiles.length){
            throw new IllegalArgumentException("widthInTiles * heightInTiles must equal the number of tiles.");
        }
        int[][] array = new int[heightInTiles * 8][widthInTiles * 8];
        for(int tileY = 0; tileY < heightInTiles; tileY++){
            for(int y = 0; y < 8; y++){
                for(int tileX = 0; tileX < widthInTiles; tileX++){
                    for(int x = 0; x < 8; x++){
                        int pixel = tiles[tileY * widthInTiles + tileX].getPixelAt(x, y);
                        array[tileY * 8 + y][tileX * 8 + x] = pixel;
                    }
                }
            }
        }
        return array;
    }

    /**
     * Converts the GBATiles to a 1-dimensional array.
     * Equivalent to all tiles converted to a 1D array through GBATile.to1DArray, then concatenated
     * together.
     * @return A 1D array describing this GBATiles.
     */
    public int[] to1DArray(){
        int[] array = new int[tiles.length * 64];
        for(int tile = 0; tile < tiles.length; tile++){
            System.arraycopy(tiles[tile].to1DArray(), 0, array, tile * 64, 64);
        }
        return array;
    }

    /**
     * Get the HexReader for a GBATiles object.
     * @param depth The bitdepth to use.
     * @param numberOfTiles The number of tiles to read.
     * @return
     */
    public static HexReader<GBATiles> getHexReader(BitDepth depth, int numberOfTiles){
        return iterator -> {
            GBATile[] tiles = new GBATile[numberOfTiles];
            for(int count = 0; count < numberOfTiles; count++){
                GBATile tile = GBATile.getHexReader(depth).read(iterator);
                tiles[count] = tile;
            }
            return new GBATiles(depth, tiles);
        };
    }

    /**
     * Get the HexWriter for a GBATiles object.
     * @param depth The bitdepth to use.
     * @param numberOfTiles The number of tiles to read.
     * @return
     */
    public static HexWriter<GBATiles> getHexWriter(BitDepth depth, int numberOfTiles){
        return (object, iterator) -> {
            for(int count = 0; count < numberOfTiles; count++){
                GBATile.getHexWriter(depth).write(object.getTile(count), iterator);
            }
        };
    }

    /**
     * Get number of tiles in this collection.
     * @return The number of tiles
     */
    public int getNumberOfTiles() {
        return tiles.length;
    }

    /**
     * Get the tiles in this collection.
     * @return The tiles.
     */
    public GBATile[] getTiles(){
        return Arrays.copyOf(tiles, tiles.length);
    }
}
