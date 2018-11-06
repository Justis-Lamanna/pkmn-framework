package com.github.lucbui.gba;

import com.github.lucbui.gba.gfx.GBAColor;
import com.github.lucbui.gba.gfx.GBAGraphic;
import com.github.lucbui.gba.gfx.GBAPalette;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class GBAUtils {
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
