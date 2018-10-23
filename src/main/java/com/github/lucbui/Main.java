package com.github.lucbui;

import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.framework.RepointStrategy;
import com.github.lucbui.framework.RepointUtils;
import com.github.lucbui.gba.GBAFrameworkFactory;
import com.github.lucbui.gba.GBAPointer;
import com.github.lucbui.gba.gfx.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework pkmnGame = PkmnFramework
                .init("C:\\Users\\laman\\Desktop\\Procession of Glazed\\ROMs\\Um\\Glazed2.gba")
                .frameworkFactory(new GBAFrameworkFactory())
                .start();
        PkmnFramework sandbox = PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .frameworkFactory(new GBAFrameworkFactory())
                .start();

        long ptr = 0x4975F8;
        GBATiles tiles = pkmnGame.read(ptr, GBATiles.getHexReader(BitDepth.FOUR, 8));

        sandbox.write(0, GBATiles.getHexWriter(BitDepth.FOUR, 8), tiles);
    }
}
