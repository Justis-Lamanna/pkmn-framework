package com.github.lucbui.gba;

import com.github.lucbui.gba.gfx.GBAColor;
import com.github.lucbui.gba.gfx.GBAGraphic;
import com.github.lucbui.gba.gfx.GBAPalette;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

public class GBAUtils {

    /**
     * A 16-color grayscale pattern, from black to white.
     */
    public static final GBAPalette BLACK_TO_WHITE_16 = grayscalePalette(16);

    /**
     * A 256-color grayscale pattern, from black to white.
     */
    public static final GBAPalette BLACK_TO_WHITE_256 = grayscalePalette(256);

    /**
     * A 16-color palette, matching the Windows VGA Palette.
     */
    public static final GBAPalette VGA_COLORS = GBAPalette.builder()
            .with(GBAColor.from(31, 31, 31)) //White
            .with(GBAColor.from(23, 23, 23)) //Silver
            .with(GBAColor.from(15, 15, 15)) //Gray
            .with(GBAColor.from(0, 0, 0)) //Black
            .with(GBAColor.from(31, 0, 0)) //Red
            .with(GBAColor.from(15, 0, 0)) //Maroon
            .with(GBAColor.from(31, 31, 0)) //Yellow
            .with(GBAColor.from(15, 15, 0)) //Olive
            .with(GBAColor.from(0, 31, 0)) //Lime
            .with(GBAColor.from(0, 15, 0)) //Green
            .with(GBAColor.from(0, 31, 31)) //Aqua
            .with(GBAColor.from(0, 15, 15)) //Teal
            .with(GBAColor.from(0, 0, 31)) //Blue
            .with(GBAColor.from(0, 0, 15)) //Navy
            .with(GBAColor.from(31, 0, 31)) //Fuschia
            .with(GBAColor.from(15, 0, 15)) //Purple
            .build();

    private GBAUtils(){
        //
    }

    /**
     * Create an image from a graphic and a palette.
     * @param graphic The tile to use.
     * @param palette The palette to use.
     * @return A bufferedimage created from the specified graphic and palette.
     */
    public static BufferedImage createImage(GBAGraphic graphic, GBAPalette palette){
        Objects.requireNonNull(graphic);
        Objects.requireNonNull(palette);
        BufferedImage img = new BufferedImage( graphic.getWidth(), graphic.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        //TODO: Convert this into a proper Indexed image?
        int[] pixels1D = Arrays.stream(graphic.to1DArray()).map(i -> palette.get(i).getColor().getRGB()).toArray();
        img.setRGB(0, 0, graphic.getWidth(), graphic.getHeight(), pixels1D, 0, graphic.getWidth());
        return img;
    }

    /**
     * Generate a palette from Black to White
     * @param size
     * @return
     */
    private static GBAPalette grayscalePalette(int size){
        if(size <= 0){
            throw new IllegalArgumentException("Size must be >0");
        }
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
    private static GBAPalette reverseGrayscale(int size){
        if(size <= 0){
            throw new IllegalArgumentException("Size must be >0");
        }
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
