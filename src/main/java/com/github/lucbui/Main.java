package com.github.lucbui;

import com.github.lucbui.framework.RepointUtils;
import com.github.lucbui.file.GBAPointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.structures.SampleStructure;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .addReaderWriter(GBAPointer.class, GBAPointer.HEX_READER, GBAPointer.HEX_WRITER)
                .start();
        SampleStructure ss = PkmnFramework.read(0x4, SampleStructure.class);
        PkmnFramework.write(0x14, ss, RepointUtils.identityRepointStrategy());
        System.out.println(ss);
    }
}
