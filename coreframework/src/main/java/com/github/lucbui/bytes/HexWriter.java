package com.github.lucbui.bytes;

import com.github.lucbui.file.HexFieldIterator;

/**
 * A common interface for object-byte translation.
 * @param <T> The type of object being written from
 */
public interface HexWriter<T> {

    /**
     * Translate the object into a sequence of bytes.
     * @param object The object to write.
     * @param iterator The iterator to write into.
     */
    void write(T object, HexFieldIterator iterator);

    /**
     * Force a write of an object.
     * @param object The object to write.
     * @param iterator The iterator to write with.
     */
    default void writeObject(Object object, HexFieldIterator iterator){
        write((T) object, iterator);
    }
}
