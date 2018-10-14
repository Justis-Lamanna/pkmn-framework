package com.github.lucbui;

import com.github.lucbui.config.Configuration;
import com.github.lucbui.config.MapConfiguration;
import com.github.lucbui.config.MergedConfiguration;
import com.github.lucbui.framework.RepointUtils;
import com.github.lucbui.file.GBAPointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.structures.SampleStructure;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        Configuration one = new MapConfiguration("hello", "hi", "goodbye", "cya");
        Configuration two = new MapConfiguration("hello", "hola");
        PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .addReaderWriter(GBAPointer.class, GBAPointer.HEX_READER, GBAPointer.HEX_WRITER)
                .setConfiguration(new MergedConfiguration(one, two))
                .start();
        System.out.println(PkmnFramework.getFromConfig("hello", null));
        System.out.println(PkmnFramework.getFromConfig("goodbye", null));
    }
}
