package com.github.lucbui.gba.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.annotations.Palette;
import com.github.lucbui.gba.gfx.GBAPalette;
import com.github.lucbui.pipeline.PointerFieldFriendlyDoublePipe;

/**
 * A pipe which reads a palette using @Palette annotation
 */
public class PaletteFieldPipe implements PointerFieldFriendlyDoublePipe {

    @Override
    public Object makeObject(FieldObject object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        int numberOfColors = object.getAnnotation(Palette.class).value();
        return GBAPalette.getHexer(numberOfColors).read(iterator);
    }

    @Override
    public void writeObject(HexFieldIterator iterator, FieldObject object, PkmnFramework pkmnFramework) {
        int numberOfColors = object.getAnnotation(Palette.class).value();
        GBAPalette.getHexer(numberOfColors).writeObject(object.getReferent(), iterator);
    }
}