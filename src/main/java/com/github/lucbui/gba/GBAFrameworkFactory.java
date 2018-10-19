package com.github.lucbui.gba;

import com.github.lucbui.framework.FrameworkFactory;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.gfx.GBAColor;
import com.github.lucbui.gba.gfx.GBAPaletteConfig;

/**
 * A FrameworkFactory which works with GBA games specifically.
 */
public class GBAFrameworkFactory implements FrameworkFactory {

    @Override
    public void configure(PkmnFramework.Builder builder) {
        //GBA-Style pointers
        builder.addReaderWriter(GBAPointer.class, GBAPointer.HEX_READER, GBAPointer.HEX_WRITER);
        //GBA-Style colors
        builder.addReaderWriter(GBAColor.class, GBAColor.HEX_READER, GBAColor.HEX_WRITER);
        //@GBAPaletteConfig annotation
        builder.addReflectionAnnotationFunction(GBAPaletteConfig.class, new GBAPaletteConfig.Annotation());
    }
}
