package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.LinearPipeline;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.utility.HexerUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * A pipe which reads @PointerField annotations.
 * This annotation uses @Offset, so it is recommended to place this before the OffsetReadPipe.
 *
 * If a field is populated, it is not modified.
 */
public class PointerObjectReadPipe implements ReadPipe {

    @Override
    public void read(Object object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        List<Field> fields = PipeUtils.getNullAnnotatedFields(object, PointerField.class);
        for(Field field : fields){
            if(!field.isAnnotationPresent(Offset.class)){
                throw new ReadPipeException("PointerField must be annotated with @Offset");
            }
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
                FieldUtils.writeDeclaredField(object, field.getName(), new PointerObject<>(ptr, obj), true);
            } catch (IllegalAccessException e) {
                throw new ReadPipeException("Error writing field", e);
            }
        }
    }
}
