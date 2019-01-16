package com.github.lucbui.gba.pipes;

import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.GBAPointer;
import com.github.lucbui.gba.annotations.Palette;
import com.github.lucbui.gba.gfx.GBAPalette;
import com.github.lucbui.pipeline.DoublePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;

/**
 * A pipe which reads a palette
 */
public class PaletteFieldPipe implements DoublePipe<FieldObject> {
    @Override
    public void read(FieldObject object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        Palette palette = object.getField().getAnnotation(Palette.class);
        Hexer<GBAPalette> hexer = GBAPalette.getHexer(palette.value());
        object.set(hexer.read(iterator)).or(ReadPipeException::new);
    }

    @Override
    public void write(HexFieldIterator iterator, FieldObject object, PkmnFramework pkmnFramework) {
        Palette palette = object.getField().getAnnotation(Palette.class);
        Hexer<GBAPalette> hexer = GBAPalette.getHexer(palette.value());
        hexer.writeObject(object.getReferent(), iterator);
    }
}
