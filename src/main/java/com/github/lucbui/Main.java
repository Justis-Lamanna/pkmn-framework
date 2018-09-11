package com.github.lucbui;

import com.github.lucbui.file.GBAPointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.structures.SampleStructure;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .addReader(GBAPointer.class, GBAPointer.HEX_READER)
                .start();
        System.out.println(PkmnFramework.read(0, SampleStructure.class));
    }
}
