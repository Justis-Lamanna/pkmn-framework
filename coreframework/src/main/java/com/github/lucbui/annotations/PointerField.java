package com.github.lucbui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a PointerField for a structure, which stores an object + a pointer to that object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PointerField {

    /**
     * The type of object.
     * @return
     */
    Class<? extends Object> objectType();
}
