package com.github.lucbui.structures;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.annotations.StructField;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.gba.GBAPointer;
import com.github.lucbui.gba.gfx.GBAPalette;
import com.github.lucbui.gba.gfx.GBAPaletteConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A sample data structure, to practice working with the @DataStructure annotation.
 */
@DataStructure(size = 8)
public class SampleStructure {

    @StructField(offset = 0)
    @PointerField(pointerType = GBAPointer.class, objectType = GBAPalette.class)
    @GBAPaletteConfig(size = 4)
    public PointerObject<GBAPointer, GBAPalette> palette;

    public SampleStructure(){

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
