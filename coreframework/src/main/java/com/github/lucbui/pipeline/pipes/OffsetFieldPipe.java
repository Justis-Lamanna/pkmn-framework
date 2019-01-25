package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.HexFramework;
import com.github.lucbui.pipeline.PointerFieldFriendlyDoublePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.pipeline.exceptions.WritePipeException;
import com.github.lucbui.utility.HexerUtils;

import java.util.Optional;

public class OffsetFieldPipe implements PointerFieldFriendlyDoublePipe {
    @Override
    public Object makeObject(FieldObject object, HexFieldIterator iterator, HexFramework hexFramework) {
        return HexerUtils.getHexerFor(hexFramework.getHexers(), object.getFieldClass())
                .map(hexer -> (Object)hexer.read(iterator))
                .orElseGet(() -> {
                    if(object.isAnnotationPresent(DataStructure.class)){
                        Object obj = hexFramework.getCreateStrategy().create(object.getFieldClass());
                        hexFramework.getPipeline().modify(iterator.copy(), obj, hexFramework);
                        return obj;
                    } else {
                        String extraInfo;
                        if(object.getFieldClass().equals(PointerObject.class)){
                            extraInfo = "You must annotate this field with @PointerField";
                        } else {
                            extraInfo = "You need to register this type's hexer, or annotate its declaration with @DataStructure.";
                        }
                        throw new ReadPipeException("Unable to parse object of type: " + object.getFieldClass() + ". " + extraInfo);
                    }
                });
    }

    @Override
    public void writeObject(HexFieldIterator iterator, FieldObject object, HexFramework hexFramework) {
        Optional<? extends Hexer<?>> hexer = HexerUtils.getHexerFor(hexFramework.getHexers(), object.getFieldClass());
        if(hexer.isPresent()){
            hexer.get().writeObject(object.getReferent(), iterator);
        } else {
            if(object.isAnnotationPresent(DataStructure.class)){
                hexFramework.getPipeline().write(iterator.copy(), object.getReferent(), hexFramework);
            } else {
                String extraInfo;
                if(object.getFieldClass().equals(PointerObject.class)){
                    extraInfo = "You must annotate this field with @PointerField";
                } else {
                    extraInfo = "You need to register this type's hexer, or annotate its declaration with @DataStructure.";
                }
                throw new WritePipeException("Unable to parse object of type: " + object.getFieldClass() + ". " + extraInfo);
            }
        }
    }
/*
    @Override
    public void write(HexFieldIterator iterator, FieldObject o, HexFramework pkmnFramework) {

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
    }*/
}
