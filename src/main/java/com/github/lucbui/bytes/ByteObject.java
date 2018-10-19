package com.github.lucbui.bytes;

/**
 * A common interface to all byte objects.
 * @param <T> The subobject.
 */
public interface ByteObject<T> {

    /**
     * Get the size of a ByteObject.
     * @return The byte's size, as a long.
     */
    long getValue();

    /**
     * Create a new instance of this object with a new size.
     * @param newValue The new size.
     * @return The new instance of the object.
     */
    T newInstance(long newValue);
}
