package com.github.lucbui.pipeline;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.RepointStrategy;

/**
 * An abstraction of a pipeline, which translates an object to and from bytes in some way
 */
public interface Pipeline {
    /**
     * Read an instance of the specified object.
     * @param iterator The iterator to use.
     * @param clazz The class of object to create
     * @param <T> The type of object to create
     * @return The created object
     */
    <T> T read(HexFieldIterator iterator, Class<T> clazz);

    /**
     * Write an instance of the specified object to an iterator
     * @param iterator The iterator to use
     * @param obj The object to write
     * @param repointStrategy Repoint strategy to use when writing PointerObjects
     */
    void write(HexFieldIterator iterator, Object obj, RepointStrategy repointStrategy);
}
