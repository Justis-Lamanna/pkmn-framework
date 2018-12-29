package com.github.lucbui.pipeline;

import com.github.lucbui.file.HexFieldIterator;

/**
 * An abstraction of a pipeline, which translates an object to and from bytes in some way
 * @param <T> The type of object to create
 */
public interface Pipeline<T> {
    /**
     * Modify an existing object through the pipeline
     * @param iterator The iterator to use
     * @param obj The object to modify
     */
    void modify(HexFieldIterator iterator, T obj);

    /**
     * Write an instance of the specified object to an iterator
     * @param iterator The iterator to use
     * @param obj The object to write
     */
    void write(HexFieldIterator iterator, T obj);
}
