package com.github.lucbui.gba.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.HexFramework;
import com.github.lucbui.gba.annotations.Sprite;
import com.github.lucbui.gba.gfx.GBASprite;
import com.github.lucbui.pipeline.PointerFieldFriendlyDoublePipe;

/**
 * A pipe which reads a sprite using @Sprite annotation
 */
public class SpriteFieldPipe implements PointerFieldFriendlyDoublePipe {

    @Override
    public Object makeObject(FieldObject object, HexFieldIterator iterator, HexFramework pkmnFramework) {
        Sprite spriteAnnotation = object.getAnnotation(Sprite.class);
        return GBASprite.getHexer(spriteAnnotation.bitDepth(), spriteAnnotation.size()).read(iterator);
    }

    @Override
    public void writeObject(HexFieldIterator iterator, FieldObject object, HexFramework pkmnFramework) {
        Sprite spriteAnnotation = object.getAnnotation(Sprite.class);
        GBASprite.getHexer(spriteAnnotation.bitDepth(), spriteAnnotation.size()).writeObject(object.getReferent(), iterator);
    }
}