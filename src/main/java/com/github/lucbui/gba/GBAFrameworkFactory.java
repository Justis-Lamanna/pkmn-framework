package com.github.lucbui.gba;

import com.github.lucbui.annotations.Offset;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.bytes.UnsignedShort;
import com.github.lucbui.bytes.UnsignedWord;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.FrameworkFactory;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.gba.annotations.Palette;
import com.github.lucbui.gba.annotations.Sprite;
import com.github.lucbui.gba.pipes.PaletteFieldPipe;
import com.github.lucbui.gba.pipes.SpriteFieldPipe;
import com.github.lucbui.pipeline.LinearPipeline;
import com.github.lucbui.pipeline.Pipeline;
import com.github.lucbui.pipeline.pipes.*;
import com.github.lucbui.utility.PipeUtils;

/**
 * A FrameworkFactory which works with GBA games specifically.
 */
public class GBAFrameworkFactory implements FrameworkFactory {

    @Override
    public void configure(PkmnFramework.Builder builder) {
        //Hexers
        builder.addHexer(UnsignedByte.class, UnsignedByte.HEXER);
        builder.addHexer(UnsignedShort.class, UnsignedShort.HEXER);
        builder.addHexer(UnsignedWord.class, UnsignedWord.HEXER);
        builder.addHexer(GBAPointer.class, GBAPointer.HEXER);
        //Pipeline - allows for @Palette and @Sprite annotations
        builder.setPipeline(createPipeline());
    }

    private Pipeline<Object> createPipeline(){
        return LinearPipeline.create()
                .write(new PrintPipe())
                .write(new BeforeWritePipe())
                .pipe(ForEachPipe.create(o -> PipeUtils.getAnnotatedFieldObject(o, Offset.class))
                        .pipe(new OffsetParsePipe()) //Populates the Offset, and moves the iterator to the correct place
                        .pipe(SwitchPipe.<FieldObject>create()
                                .iff(fo -> fo.isAnnotationPresent(Palette.class))
                                    .pipe(new PaletteFieldPipe())
                                    .build()
                                .iff(fo -> fo.isAnnotationPresent(Sprite.class))
                                    .pipe(new SpriteFieldPipe())
                                    .build()
                                .elsee()
                                    .pipe(new OffsetFieldPipe())
                                    .build()
                                .end())//Read/write the object from the registered hexers, or reflectively
                        .read(new SetFieldPipe()) //Set the field to the calculated value
                        .build()
                )
                .read(new AfterReadPipe())
                .read(new PrintPipe())
                .build();
    }
}
