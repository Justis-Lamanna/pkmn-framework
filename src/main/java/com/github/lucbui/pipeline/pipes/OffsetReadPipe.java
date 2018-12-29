package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.utility.HexerUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * A pipe which reads raw @Offset annotations.
 * A field with an @Offset annotation must be of a type with either a registered hexer, or is annotated @DataStructure.
 * If annotated with @DataStructure, the sub-object is created through the same pipeline creating this object.
 *
 * If a field is populated, it is not modified.
 */
public class OffsetReadPipe implements ReadPipe {

    @Override
    public void read(Object object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        List<Field> fields = PipeUtils.getNullAnnotatedFields(object, Offset.class);
        for(Field field : fields){
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
                            return pkmnFramework.getPipeline().read(iteratorForField, field.getType());
                        } else {
                            return null;
                        }
                    });
            try {
                FieldUtils.writeDeclaredField(object, field.getName(), readObject, true);
            } catch (IllegalAccessException e) {
                throw new ReadPipeException("Error writing field", e);
            }
        }
    }
}
