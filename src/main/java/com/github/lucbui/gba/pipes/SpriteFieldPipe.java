package com.github.lucbui.gba.pipes;

import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.annotations.Sprite;
import com.github.lucbui.gba.gfx.GBASprite;
import com.github.lucbui.pipeline.DoublePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;

/**
 * A pipe which reads a sprite
 */
public class SpriteFieldPipe implements DoublePipe<FieldObject> {
    @Override
    public void read(FieldObject object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        Sprite spriteAnnot = object.getField().getAnnotation(Sprite.class);
        Hexer<GBASprite> hexer = GBASprite.getHexer(spriteAnnot.bitDepth(), spriteAnnot.size());
        object.set(hexer.read(iterator)).or(ReadPipeException::new);
    }

    @Override
    public void write(HexFieldIterator iterator, FieldObject object, PkmnFramework pkmnFramework) {
        Sprite spriteAnnot = object.getField().getAnnotation(Sprite.class);
        Hexer<GBASprite> hexer = GBASprite.getHexer(spriteAnnot.bitDepth(), spriteAnnot.size());
        hexer.writeObject(object.getReferent(), iterator);
    }
}
