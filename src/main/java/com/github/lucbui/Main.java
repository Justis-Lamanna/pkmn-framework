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
                .addReader(GBAPointer.class, GBAPointer.HEX_READER)
                .start();
        //System.out.println(PkmnFramework.read(0, SampleStructure.class));
        PkmnFramework.write(0, GBAPointer.HEX_WRITER, GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000));
        //System.out.println(Arrays.toString(UnsignedShort.valueOf(1000).toBytes().array()));
    }
}
