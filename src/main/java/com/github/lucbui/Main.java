package com.github.lucbui;

import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.GBAFrameworkFactory;
import com.github.lucbui.gba.GBAUtils;
import com.github.lucbui.gba.gfx.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework pkmnGame = PkmnFramework
                .init("C:\\Users\\laman\\Desktop\\Procession of Glazed\\ROMs\\Um\\Glazed2.gba")
                .frameworkFactory(new GBAFrameworkFactory())
                .start();

        long ptr = 0x4975F8;
        GBASprite tiles = pkmnGame.read(ptr, GBASprite.getHexReader(BitDepth.FOUR, SpriteSize.VERTICAL_2));

        tiles = tiles.modify().setPixel(10, 5, 10).create();

        BufferedImage img = GBAUtils.createImage(tiles, GBAUtils.VGA_COLORS);
        ImageIO.write(img, "png", new File("test.png"));
    }
}
