package com.github.lucbui;

import com.github.lucbui.bytes.Bitmask;
import com.github.lucbui.config.Configuration;
import com.github.lucbui.config.MapConfig;
import com.github.lucbui.file.GBAPointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.gfx.GBAColor;
import com.github.lucbui.structures.SampleStructure;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .addReaderWriter(GBAPointer.class, GBAPointer.HEX_READER, GBAPointer.HEX_WRITER)
                .addReaderWriter(GBAColor.class, GBAColor.HEX_READER, GBAColor.HEX_WRITER)
                .start();
        PkmnFramework.write(0, GBAColor.from(10, 20, 30));
        //System.out.println(PkmnFramework.read(0, GBAColor.class));
    }
}
