package com.github.lucbui;

import com.github.lucbui.config.Configuration;
import com.github.lucbui.config.MapConfig;
import com.github.lucbui.file.GBAPointer;
import com.github.lucbui.framework.PkmnFramework;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        Configuration one = new MapConfig("hello", "hi", "goodbye", "cya");
        PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .addReaderWriter(GBAPointer.class, GBAPointer.HEX_READER, GBAPointer.HEX_WRITER)
                .setConfiguration(one)
                .start();
        System.out.println(PkmnFramework.getFromConfig("hello", null));
        PkmnFramework.setInConfig("hello", "nyan");
        System.out.println(PkmnFramework.getFromConfig("hello", null));
    }
}
