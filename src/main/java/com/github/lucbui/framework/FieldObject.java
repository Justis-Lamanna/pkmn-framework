package com.github.lucbui.framework;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * A class which encapsulates an object, a field, and the value of the field.
 */
public class FieldObject {
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

    /**
     * Get the original object parsed by the field.
     * @return
     */
    public Object getParent() {
        return parent;
    }

    /**
     * Get the field represented by this FieldObject
     * @return
     */
    public Field getField() {
        return field;
    }

    /**
     * Get the object referenced by the field
     * Essentially, reference = parent.field;
     * @return
     */
    public Object getReferent() {
        return referent;
    }

    @Override
    public String toString() {
        return "FieldObject{" +
                "field=" + field.getName() +
                ", referent=" + referent +
                '}';
    }
}
