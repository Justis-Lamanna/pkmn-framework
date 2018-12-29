package com.github.lucbui.framework;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Optional;

public class FieldObject {
    private Object parent;
    private Field field;
    private Object referent;

    private FieldObject(Object parent, Field field, Object referent) {
        this.parent = parent;
        this.field = field;
        this.referent = referent;
    }

    public static Optional<FieldObject> get(Object obj, Field field) {
        try {
            Object referent = FieldUtils.readField(field, obj);
            return Optional.of(new FieldObject(obj, field, referent));
        } catch (IllegalAccessException e) {
            return Optional.empty();
        }
    }

    public Object getParent() {
        return parent;
    }

    public Field getField() {
        return field;
    }

    public Object getReferent() {
        return referent;
    }
}
