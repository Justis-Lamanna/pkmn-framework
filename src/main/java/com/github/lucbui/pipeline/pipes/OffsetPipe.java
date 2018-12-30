package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.WritePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.pipeline.exceptions.WritePipeException;
import com.github.lucbui.utility.HexerUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * A pipe which reads raw @Offset annotations.
 * A field with an @Offset annotation must be of a type with either a registered hexer, or is annotated @DataStructure.
 * If annotated with @DataStructure, the sub-object is created through the same pipeline creating this object.
 *
 * If a field is populated, it is not modified.
 */
public class OffsetPipe implements ReadPipe<Object>, WritePipe<Object> {

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
                            Object obj = pkmnFramework.getCreateStrategy().create(field.getType());
                            pkmnFramework.getPipeline().modify(iteratorForField, obj, pkmnFramework);
                            return obj;
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

    @Override
    public void write(HexFieldIterator iterator, Object object, PkmnFramework pkmnFramework) {
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(object.getClass(), Offset.class);
        for(Field field : fields){
            try {
                Object fieldObject = FieldUtils.readDeclaredField(object, field.getName(), true);

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
            } catch (IllegalAccessException e) {
                throw new WritePipeException("Error acccessing field " + field.getName(), e);
            }
        }
    }
}
