package com.github.lucbui.annotations;

import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.NoRepointStrategy;
import com.github.lucbui.framework.RepointStrategy;

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
     * The type of pointer.
     * @return
     */
    Class<? extends Pointer> pointerType() default Pointer.class;

    /**
     * The type of object.
     * @return
     */
    Class<? extends Object> objectType() default Void.class;

    /**
     * The repoint strategy to use.
     * Default forbids repointing.
     * Note that the specified class MUST have a default empty constructor.
     * @return
     */
    Class<? extends RepointStrategy> repointStrategy() default NoRepointStrategy.class;
}
