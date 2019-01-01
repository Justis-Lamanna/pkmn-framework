package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.Offset;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.pipeline.LinearPipeline;
import com.github.lucbui.pipeline.Pipeline;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PipeUtils {
    private PipeUtils(){

    }

    /**
     * Gets all non-null fields annotated with a specified annotation.
     * @param obj The object to inspect
     * @param annotationClass The annotation to search for
     * @return The fields matching the criteria
     */
    public static List<Field> getNullAnnotatedFields(Object obj, Class<? extends Annotation> annotationClass){
        return FieldUtils.getFieldsListWithAnnotation(obj.getClass(), annotationClass).stream()
                .filter(f -> {
                    try {
                        return FieldUtils.readDeclaredField(obj, f.getName(), true) == null;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                }).collect(Collectors.toList());
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
                .write(new BeforeWritePipe())
                .pipe(new PointerObjectPipe())
                .pipe(new OffsetPipe())
                .read(new AfterReadPipe())
                .pipe(ForEachPipe.create(o -> PipeUtils.getAnnotatedFieldObject(o, Offset.class))
                    .pipe(new PrintPipe()).build()
                )
                .build();
    }
}
