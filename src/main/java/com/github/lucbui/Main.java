package com.github.lucbui;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.DataStructureSize;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.config.MapConfig;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.ConfigurationEvaluator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.GBAFrameworkFactory;
import com.github.lucbui.gba.GBAPointer;
import com.github.lucbui.pipeline.LinearPipeline;
import com.github.lucbui.pipeline.Pipeline;
import com.github.lucbui.pipeline.pipes.EmptyConstructorCreatePipe;
import com.github.lucbui.pipeline.pipes.OffsetReadPipe;
import com.github.lucbui.utility.HexerUtils;

import java.io.IOException;
import java.util.Collections;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework pkmnGame = PkmnFramework
                .init("C:\\Users\\laman\\Desktop\\Procession of Glazed\\ROMs\\Um\\Glazed2.gba")
                .frameworkFactory(new GBAFrameworkFactory())
                .start();
        TestStructure ts = pkmnGame.read(GBAPointer.valueOf(0x70), TestStructure.class);
        System.out.println(ts);
        System.out.println(pkmnGame.getSize(ts));
    }

    @DataStructure
    public static class TestStructure {
        @Offset("0x0")
        public UnsignedByte b1;

        @Offset("0x1")
        @Absolute
        public UnsignedByte b2;

        @Override
        public String toString() {
            return "TestStructure{" +
                    "b1=" + b1 +
                    ", b2=" + b2 +
                    '}';
        }
    }
}
