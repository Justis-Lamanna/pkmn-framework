package com.github.lucbui.framework;

import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.utility.Try;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * A class which encapsulates an object, a field, and the value of the field.
 */
public class FieldObject {
    private Pointer pointer;
    private Object parent;
    private Field field;
    private Object referent;

    private FieldObject(Object parent, Field field, Object referent) {
        this.parent = parent;
        this.field = field;
        this.referent = referent;
    }

    /**
     * Get a FieldObject from an object and field definition
     * @param obj The object to use
     * @param field The field to use
     * @return Either a FieldObject created successfully, or an empty Optional
     */
    public static Optional<FieldObject> get(Object obj, Field field) {
        try {
            Object referent = FieldUtils.readField(field, obj, true);
            return Optional.of(new FieldObject(obj, field, referent));
        } catch (IllegalAccessException e) {
            return Optional.empty();
        }
    }

    public Pointer getPointer() {
        return pointer;
    }

    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }

    /**
     * Get the original object parsed by the field.
     * @return
     */
    public Object getParent() {
        return parent;
    }

    /**
     * Test if an annotation is present on the enclosed field
     * @param annotation The annotation to check
     * @return True, if the annotation is present
     */
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation){
        return field.isAnnotationPresent(annotation);
    }

    /**
     * Get an Annotation from the enclosed field
     * @param annotation The annotation to get
     * @param <T> The type of annotation
     * @return The annotation instance
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotation){
        return field.getAnnotation(annotation);
    }

    /**
     * Get the class of the field.
     * If @PointerField annotation is present, the objectType is returned.
     * @return
     */
    public Class<?> getFieldClass(){
        if(field.isAnnotationPresent(PointerField.class)){
            return field.getAnnotation(PointerField.class).objectType();
        }
        return field.getType();
    }

    /**
     * Get the object referenced by the field
     * Essentially, reference = parent.field;
     * @return
     */
    public Object getReferent() {
        return referent;
    }

    public void syncReferent(){
        try {
            referent = FieldUtils.readField(field, parent, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setReferent(Object referent){
        this.referent = referent;
    }

    /**
     * Set the field in the object to a new value
     */
    public Try<Object> set(){
        try {
            FieldUtils.writeDeclaredField(getParent(), field.getName(), this.referent, true);
            return Try.ok(this.referent);
        } catch (IllegalAccessException e) {
            return Try.error("Error writing field:" + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "FieldObject{" +
                "field=" + field.getName() +
                ", referent=" + referent +
                '}';
    }
}
