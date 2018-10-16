package com.github.lucbui.bytes;

/**
 * A common interface to all byte objects.
 * Currently doesn't do anything really.
 * @param <T> The subobject.
 */
public interface ByteObject<T> {

    /**
     * Get the value of a ByteObject.
     * @return The byte's value, as a long.
     */
    long getValue();
}
