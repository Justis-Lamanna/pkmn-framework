package com.github.lucbui;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.GBAFrameworkFactory;
import com.github.lucbui.gba.GBAPointer;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework pkmnGame = PkmnFramework
                .init("C:\\Users\\laman\\Desktop\\Procession of Glazed\\ROMs\\Um\\Glazed2.gba")
                .frameworkFactory(new GBAFrameworkFactory())
                .start();

        /*long ptr = 0x4975F8;
        GBASprite tiles = pkmnGame.read(ptr, GBASprite.getHexReader(BitDepth.FOUR, SpriteSize.VERTICAL_2));

        tiles = tiles.modify().setPixel(10, 5, 10).create();

        BufferedImage img = GBAUtils.createImage(tiles, GBAUtils.VGA_COLORS);
        ImageIO.write(img, "png", new File("test.png"));*/
        TestStructure ts = pkmnGame.read(GBAPointer.valueOf(0x70), TestStructure.class);
        //pkmnGame.write(GBAPointer.valueOf(0x70), ts);
        System.out.println(ts);
    }

    @DataStructure
    public static class TestStructure {
        @Offset("0x0")
        public UnsignedByte b1;

        @Offset("0x1")
        @Absolute
        public UnsignedByte b2;
    }
}
