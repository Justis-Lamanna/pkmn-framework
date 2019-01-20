package com.github.lucbui;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.bytes.TribitByte;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.GBAFrameworkFactory;
import com.github.lucbui.gba.GBAPointer;
import com.github.lucbui.gba.gfx.GBAPalette;
import com.github.lucbui.utility.RepointUtils;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework pkmnGame = PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .frameworkFactory(new GBAFrameworkFactory())
                .start();
        TestStructure ts = pkmnGame.read(GBAPointer.valueOf(0x0), TestStructure.class);
        ts.b1.setRepointStrategy(RepointUtils.identityRepointStrategy());
        ts.b1.setObject(UnsignedByte.valueOf(0xFF));
        pkmnGame.write(GBAPointer.valueOf(0x0), ts);
    }

    @DataStructure
    public static class TestStructure {
        @Offset("0x0")
        @PointerField(objectType = UnsignedByte.class)
        public PointerObject<UnsignedByte> b1;

        @Offset("0x4")
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
