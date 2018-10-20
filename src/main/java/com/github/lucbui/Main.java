package com.github.lucbui;

import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.framework.RepointStrategy;
import com.github.lucbui.framework.RepointUtils;
import com.github.lucbui.gba.GBAFrameworkFactory;
import com.github.lucbui.gba.GBAPointer;
import com.github.lucbui.gba.gfx.GBAColor;
import com.github.lucbui.gba.gfx.GBAPalette;
import com.github.lucbui.gba.gfx.GBATile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        //To be replaced with a specialized collection soon:tm:
        List<GBATile> tiles = new ArrayList<>();
        long ptr = 0x4975F8;
        for(int ctr = 0; ctr < 8; ctr++){
           tiles.add(pkmnGame.read(ptr + (ctr * 32), GBATile.getHexReader(GBATile.BitDepth.FOUR)));
        }

        for(int ctr = 0; ctr < 8; ctr++){
            sandbox.write((ctr * 32), GBATile.HEX_WRITER, tiles.get(ctr));
        }
    }
}
