package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.DoublePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.pipeline.exceptions.WritePipeException;
import com.github.lucbui.utility.HexerUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Optional;

public class OffsetFieldPipe implements DoublePipe<FieldObject> {
    @Override
    public void read(FieldObject o, HexFieldIterator iterator, PkmnFramework pkmnFramework) {

        Field field = o.getField();

        Offset offset = field.getAnnotation(Offset.class);
        long offsetAsLong = pkmnFramework.getEvaluator().evaluateLong(offset.value()).orElseThrow(ReadPipeException::new);
        HexFieldIterator iteratorForField =
                field.isAnnotationPresent(Absolute.class) ?
                        iterator.copy(offsetAsLong) :
                        iterator.copyRelative(offsetAsLong);

        Object readObject = HexerUtils.getHexerFor(pkmnFramework.getHexers(), field.getType())
                .map(hexer -> (Object)hexer.read(iteratorForField))
                .orElseGet(() -> {
                    if(field.isAnnotationPresent(DataStructure.class)){
                        Object obj = pkmnFramework.getCreateStrategy().create(field.getType());
                        pkmnFramework.getPipeline().modify(iteratorForField, obj, pkmnFramework);
                        return obj;
                    } else {
                        return null;
                    }
                });
        try {
            FieldUtils.writeDeclaredField(o.getParent(), field.getName(), readObject, true);
        } catch (IllegalAccessException e) {
            throw new ReadPipeException("Error writing field", e);
        }
    }

    @Override
    public void write(HexFieldIterator iterator, FieldObject o, PkmnFramework pkmnFramework) {

        Field field = o.getField();
        Object fieldObject = o.getReferent();

        Offset offset = field.getAnnotation(Offset.class);
        long offsetAsLong = pkmnFramework.getEvaluator().evaluateLong(offset.value()).orElseThrow(ReadPipeException::new);
        HexFieldIterator iteratorForField =
                field.isAnnotationPresent(Absolute.class) ?
                        iterator.copy(offsetAsLong) :
                        iterator.copyRelative(offsetAsLong);

        Optional<? extends Hexer<?>> hexer = HexerUtils.getHexerFor(pkmnFramework.getHexers(), field.getType());
        if(hexer.isPresent()){
            hexer.get().writeObject(fieldObject, iteratorForField);
        } else {
            if(field.isAnnotationPresent(DataStructure.class)){
                pkmnFramework.getPipeline().write(iteratorForField, field.getType(), pkmnFramework);
            } else {
                throw new ReadPipeException("Encountered object of type " + field.getType().getName() +
                        " which does not have an associated hexer, and is not marked @DataStructure.");
            }
        }
    }
}
