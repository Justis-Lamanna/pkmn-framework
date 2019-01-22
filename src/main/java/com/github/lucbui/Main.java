package com.github.lucbui;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.GBAFrameworkFactory;
import com.github.lucbui.gba.GBAPointer;
import com.github.lucbui.gba.annotations.Palette;
import com.github.lucbui.gba.gfx.GBAPalette;

public class Main {

    public static void main(String... args) {
        PkmnFramework pkmnGame = PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .frameworkFactory(new GBAFrameworkFactory())
                .build()
                .orThrow();
        TestStructure ts = pkmnGame.read(GBAPointer.valueOf(0x0), TestStructure.class);
        //ts.b1.setRepointStrategy(RepointUtils.identityRepointStrategy());
        //ts.b1.setObject(UnsignedByte.valueOf(0xFF));
        //pkmnGame.write(GBAPointer.valueOf(0x0), ts);
    }

    @DataStructure
    public static class TestStructure {
        @Offset("0x0")
        @Palette(4)
        @PointerField(objectType = GBAPalette.class)
        public PointerObject<GBAPalette> b1;

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
