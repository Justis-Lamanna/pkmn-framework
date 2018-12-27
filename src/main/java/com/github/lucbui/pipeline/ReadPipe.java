package com.github.lucbui.pipeline;

import com.github.lucbui.file.HexFieldIterator;

/**
 * A pipe which modifies an object, through usage of a HexFieldIterator
 */
public interface ReadPipe {
    /**
     * Modify an instance of an object, using a HexFieldIterator
     * @param object The object to modify
     * @param iterator The iterator to read from
     * @param pipeline The pipline
     */
    void read(Object object, HexFieldIterator iterator, LinearPipeline pipeline);
}
