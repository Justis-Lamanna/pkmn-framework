package com.github.lucbui;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.bytes.TribitByte;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.GBAFrameworkFactory;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework pkmnGame = PkmnFramework
                .init("C:\\Users\\laman\\Desktop\\Procession of Glazed\\ROMs\\Um\\Glazed2.gba")
                .frameworkFactory(new GBAFrameworkFactory())
                .start();
        //TestStructure ts = pkmnGame.read(GBAPointer.valueOf(0x14C), TestStructure.class);
        TribitByte nom = TribitByte.value(0xFD);
        TribitByte nomShR = nom.shiftRight(100);
        TribitByte nomShL = nom.shiftLeft(100);
        System.out.println(nom);
        System.out.println(nomShR);
        System.out.println(nomShL);
    }

    @DataStructure
    public static class TestStructure {
        @Offset("0x0")
        @PointerField(objectType = UnsignedByte.class)
        public PointerObject<UnsignedByte> b1;

        @Offset("0x1")
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
