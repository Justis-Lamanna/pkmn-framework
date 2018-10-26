package com.github.lucbui.gba;

import com.github.lucbui.gba.gfx.GBAColor;
import com.github.lucbui.gba.gfx.GBAPalette;
import com.github.lucbui.gba.gfx.GBATile;
import com.github.lucbui.gba.gfx.GBATiles;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class GBAUtils {
    private GBAUtils(){
        //
    }

    /**
     * Create an image from one tile and a palette.
     * @param tile The tile to use.
     * @param palette The palette to use.
     * @return
     */
    public static BufferedImage createImage(GBATile tile, GBAPalette palette){
        BufferedImage img = new BufferedImage( 8, 8, BufferedImage.TYPE_4BYTE_ABGR);
        //TODO: Convert this into a proper Indexed image?
        int[] pixels1D = Arrays.stream(tile.to1DArray()).map(i -> palette.get(i).getColor().getRGB()).toArray();
        img.setRGB(0, 0, 8, 8, pixels1D, 0, 8);
        return img;
    }

    /**
     * Create an image from a combination of tiles and palette.
     * @param tiles The tiles to use.
     * @param palette The palette to use.
     * @param widthInTiles The width, in tiles.
     * @param heightInTiles The height, in tiles.
     * @return
     */
    public static BufferedImage createImage(GBATiles tiles, GBAPalette palette, int widthInTiles, int heightInTiles){
        if(widthInTiles * heightInTiles != tiles.getNumberOfTiles()){
            throw new IllegalArgumentException("widthInTiles * heightInTiles must equal the number of tiles.");
        }
        BufferedImage img = new BufferedImage( widthInTiles * 8, heightInTiles * 8, BufferedImage.TYPE_4BYTE_ABGR);
        //int[] pixels1D = Arrays.stream(tiles.to1DArray()).map(i -> palette.get(i).getColor().getRGB()).toArray();
        //img.setRGB(0, 0, 8, 8, pixels1D, 0, 8);
        for(int yTile = 0; yTile < heightInTiles; yTile++){
            for(int xTile = 0; xTile < widthInTiles; xTile++){
                GBATile tile = tiles.getTile(xTile + (widthInTiles * yTile));
                int[] pixels1D = Arrays.stream(tile.to1DArray()).map(i -> palette.get(i).getColor().getRGB()).toArray();
                img.setRGB(xTile * 8, yTile * 8, 8, 8, pixels1D, 0, 8);
            }
        }
        return img;
    }

    /**
     * Generate a palette from Black to White
     * @param size
     * @return
     */
    public static GBAPalette grayscalePalette(int size){
        GBAPalette.Builder builder = GBAPalette.builder();
        //Interpolating! (0,0), (size, 31)
        //slope = 256 / size
        for(int count = 0; count < size; count++){
            int pixel = count * 31 / size;
            builder.with(GBAColor.from(pixel, pixel, pixel));
        }
        return builder.build();
    }

    /**
     * Generate a palette from White to Black
     * @param size
     * @return
     */
    public static GBAPalette reverseGrayscale(int size){
        GBAPalette.Builder builder = GBAPalette.builder();
        //Interpolating! (0,0), (size, 31)
        //slope = 256 / size
        for(int count = 0; count < size; count++){
            int pixel = 31 - (count * 31 / size);
            builder.with(GBAColor.from(pixel, pixel, pixel));
        }
        return builder.build();
    }
}
