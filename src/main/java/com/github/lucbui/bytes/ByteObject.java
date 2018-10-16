package com.github.lucbui.bytes;

/**
 * A common interface to all byte objects.
 * @param <T> The subobject.
 */
public interface ByteObject<T> {

    /**
     * Get the value of a ByteObject.
     * @return The byte's value, as a long.
     */
    long getValue();

    /**
     * Create a new instance of this object with a new value.
     * @param newValue The new value.
     * @return The new instance of the object.
     */
    T newInstance(long newValue);
}
