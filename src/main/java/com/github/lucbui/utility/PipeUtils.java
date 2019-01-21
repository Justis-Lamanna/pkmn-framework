package com.github.lucbui.utility;

import com.github.lucbui.annotations.Offset;
import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.pipeline.LinearPipeline;
import com.github.lucbui.pipeline.Pipeline;
import com.github.lucbui.pipeline.pipes.*;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

public class PipeUtils {
    private PipeUtils(){

    }

    /**
     * Get a Stream of FieldObjects, each representing a specifically-annotated field in an object.
     * @param obj The object to parse fields out of
     * @param annotationClass The annotation to search for
     * @return A stream of FieldObjects, Each containing the field and its corresponding object value in the object.
     */
    public static Stream<FieldObject> getAnnotatedFieldObject(Object obj, Class<? extends Annotation> annotationClass){
        return FieldUtils.getFieldsListWithAnnotation(obj.getClass(), annotationClass).stream()
                .map(f -> FieldObject.get(obj, f).orElseThrow(IllegalArgumentException::new));
    }

    /**
     * Get the default pipeline
     * @return The default pipeline to use
     */
    public static Pipeline<Object> getDefaultPipeline(){
        return LinearPipeline.create()
                .write(new PrintPipe())
                .write(new BeforeWritePipe())
                .pipe(ForEachPipe.create(o -> PipeUtils.getAnnotatedFieldObject(o, Offset.class))
                        .pipe(new OffsetParsePipe()) //Populates the Offset, and moves the iterator to the correct place
                        .pipe(new OffsetFieldPipe())
                        .read(new SetFieldPipe()) //Set the field to the calculated value
                        .build()
                )
                .read(new AfterReadPipe())
                .read(new PrintPipe())
                .build();
    }
}
