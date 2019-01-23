package com.github.lucbui.gba.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes additional information for Palettes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Palette {
    /**
     * Size of the palette
     * @return The palette's size
     */
    int value() default 16;
}
