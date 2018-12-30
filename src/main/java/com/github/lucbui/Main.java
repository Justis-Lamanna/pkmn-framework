package com.github.lucbui;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.GBAFrameworkFactory;
import com.github.lucbui.gba.GBAPointer;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework pkmnGame = PkmnFramework
                .init("C:\\Users\\laman\\Desktop\\Procession of Glazed\\ROMs\\Um\\Glazed2.gba")
                .frameworkFactory(new GBAFrameworkFactory())
                .start();
        TestStructure ts = pkmnGame.read(GBAPointer.valueOf(0x70), TestStructure.class);
        System.out.println(ts);
    }

    @DataStructure
    public static class TestStructure {
        @Offset("0x0")
        public UnsignedByte b1;

        @Offset("0x1")
        @Absolute
        public UnsignedByte b2;

        public String ignoreMe;

        @Override
        public String toString() {
            return "TestStructure{" +
                    "b1=" + b1 +
                    ", b2=" + b2 +
                    '}';
        }
    }
}
