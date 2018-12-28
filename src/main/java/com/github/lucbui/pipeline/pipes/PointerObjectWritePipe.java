package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.Offset;
import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.framework.RepointMetadata;
import com.github.lucbui.framework.RepointStrategy;
import com.github.lucbui.pipeline.LinearPipeline;
import com.github.lucbui.pipeline.WritePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.utility.HexerUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

public class PointerObjectWritePipe implements WritePipe {
    @Override
    public void write(HexFieldIterator iterator, Object object, PkmnFramework pkmnFramework) {
        List<Field> fields = PipeUtils.getNullAnnotatedFields(object, PointerField.class);
        for(Field field : fields){
            if(!field.isAnnotationPresent(Offset.class)){
                throw new ReadPipeException("PointerField must be annotated with @Offset");
            }
            PointerField pointerField = field.getAnnotation(PointerField.class);
            Offset offset = field.getAnnotation(Offset.class);
            long offsetAsLong = pkmnFramework.getEvaluator().evaluateLong(offset.value()).orElseThrow(ReadPipeException::new);

            try {
                PointerObject<?> pointerObject = (PointerObject<?>) FieldUtils.readDeclaredField(object, field.getName(), true);
                RepointStrategy repointStrategy = pointerObject.getRepointStrategy();
                Object pointerObjectObj = pointerObject.getObject();
                Pointer pointerObjectPtr = pointerObject.getPointer();

                Pointer newPointer = repointStrategy.repoint(createMetadata(pointerObject));
            } catch (IllegalAccessException e) {
                throw new ReadPipeException("Error reading field", e);
            }
        }
    }

    private RepointMetadata createMetadata(PointerObject<?> pointerObject) {
        return new RepointMetadata(pointerObject);
    }
}
