package com.github.lucbui;

import com.github.lucbui.file.GBAPointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.structures.SampleStructure;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .addReader(GBAPointer.class, GBAPointer.HEX_READER)
                .start();
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0, 0, 0});
        PkmnFramework.getIterator(4).writeRelative(0, bb);
    }
}
