package com.github.lucbui;

import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.bytes.UnsignedShort;
import com.github.lucbui.bytes.UnsignedWord;
import com.github.lucbui.file.GBAPointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.structures.SampleStructure;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .addReaderWriter(GBAPointer.class, GBAPointer.HEX_READER, GBAPointer.HEX_WRITER)
                .start();
        //System.out.println(PkmnFramework.read(0x40, SampleStructure.class));
        PkmnFramework.write(0x30, PkmnFramework.read(0x40, SampleStructure.class));
        //PkmnFramework.write(0, GBAPointer.HEX_WRITER, GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000));
    }
}
