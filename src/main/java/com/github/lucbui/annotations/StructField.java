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
     * The class to read this as.
     * @return
     */
    Class<?> readAs() default Void.class;

    /**
     * The type of StructField: Nested, or Pointer.
     * Default is Nested. A Pointer fieldType indicates that a pointer should be read at the specified {@code value}, and
     * the contained object begins parsing at that pointer. A Nested fieldType indicates that the object should be read
     * immediately from the specified {@code value}.
     * @return
     */
    StructFieldType fieldType() default StructFieldType.NESTED;
}
