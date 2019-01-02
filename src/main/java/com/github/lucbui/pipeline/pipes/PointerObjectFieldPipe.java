package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.bytes.RepointMetadata;
import com.github.lucbui.bytes.RepointStrategy;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.DoublePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.utility.HexerUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

public class PointerObjectFieldPipe implements DoublePipe<FieldObject> {
    @Override
    public void read(FieldObject o, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        Field field = o.getField();

        PointerField pointerField = field.getAnnotation(PointerField.class);
        Offset offset = field.getAnnotation(Offset.class);
        long offsetAsLong = pkmnFramework.getEvaluator().evaluateLong(offset.value()).orElseThrow(ReadPipeException::new);
        HexFieldIterator iteratorForField =
                field.isAnnotationPresent(Absolute.class) ?
                        iterator.copy(offsetAsLong) :
                        iterator.copyRelative(offsetAsLong);

        Pointer ptr = HexerUtils.getHexerFor(pkmnFramework.getHexers(), Pointer.class)
                .map(hexer -> hexer.read(iteratorForField))
                .orElseThrow(ReadPipeException::new);

        HexFieldIterator iteratorForNestedObject = iterator.copy(ptr.getLocation());
        Object obj = HexerUtils.getHexerFor(pkmnFramework.getHexers(), pointerField.objectType())
                .map(hexer -> hexer.read(iteratorForNestedObject))
                .orElseThrow(ReadPipeException::new);

        try {
            FieldUtils.writeDeclaredField(o.getParent(), field.getName(), new PointerObject<>(ptr, obj), true);
        } catch (IllegalAccessException e) {
            throw new ReadPipeException("Error writing field", e);
        }
    }

    @Override
    public void write(HexFieldIterator iterator, FieldObject o, PkmnFramework pkmnFramework) {

        Field field = o.getField();

        if(!field.isAnnotationPresent(Offset.class)){
            throw new ReadPipeException("PointerField must be annotated with @Offset");
        }
        PointerField pointerField = field.getAnnotation(PointerField.class);
        Offset offset = field.getAnnotation(Offset.class);
        long offsetAsLong = pkmnFramework.getEvaluator().evaluateLong(offset.value()).orElseThrow(ReadPipeException::new);

        PointerObject<?> pointerObject = (PointerObject<?>) o.getReferent();
        RepointStrategy repointStrategy = pointerObject.getRepointStrategy();
        Object pointerObjectObj = pointerObject.getObject();
        Pointer pointerObjectPtr = pointerObject.getPointer();

        Pointer newPointer = repointStrategy.repoint(createMetadata(pointerObject));
    }

    private RepointMetadata createMetadata(PointerObject<?> pointerObject) {
        return new RepointMetadata(pointerObject);
    }
}
