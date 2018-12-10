package com.github.lucbui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an annotation for an arithmetic string that calculates an offset.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Offset {

    /**
     * The offset of the field inside the raw data structure. For example, if size is set to 1, the matching field would
     * be parsed, starting at [pointer] + 1.
     * @return
     */
    String value();
}
