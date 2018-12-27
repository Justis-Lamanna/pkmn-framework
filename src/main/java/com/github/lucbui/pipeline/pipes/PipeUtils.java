package com.github.lucbui.pipeline.pipes;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

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
}
