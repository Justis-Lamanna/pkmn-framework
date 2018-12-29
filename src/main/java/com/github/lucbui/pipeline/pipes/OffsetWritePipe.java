package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.WritePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.pipeline.exceptions.WritePipeException;
import com.github.lucbui.utility.HexerUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class OffsetWritePipe implements WritePipe {

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
                        pkmnFramework.getPipeline().write(iteratorForField, field.getType());
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
