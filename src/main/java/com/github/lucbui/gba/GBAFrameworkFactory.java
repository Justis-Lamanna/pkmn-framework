package com.github.lucbui.gba;

import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.bytes.UnsignedShort;
import com.github.lucbui.bytes.UnsignedWord;
import com.github.lucbui.framework.FrameworkFactory;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.gfx.GBAColor;
import com.github.lucbui.gba.gfx.GBAMapTileMetadata;

/**
 * A FrameworkFactory which works with GBA games specifically.
 */
public class GBAFrameworkFactory implements FrameworkFactory {

    @Override
    public void configure(PkmnFramework.Builder builder) {
        //Hexers
        //GBA-Style pointers
        builder.addHexer(UnsignedByte.class, UnsignedByte.HEXER);
        builder.addHexer(UnsignedShort.class, UnsignedShort.HEXER);
        builder.addHexer(UnsignedWord.class, UnsignedWord.HEXER);
        builder.addHexer(GBAPointer.class, GBAPointer.HEXER);
    }
}
