package com.github.lucbui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field for the enclosed data structure.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StructField {
    /**
     * The offset of the field inside the raw data structure. For example, if value is set to 1, the matching field would
     * be parsed, starting at [pointer] + 1.
     * @return
     */
    int value();

    /**
     * If true, this field is read-only.
     * @return
     */
    boolean readOnly() default false;
}
