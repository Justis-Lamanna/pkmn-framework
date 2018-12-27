package com.github.lucbui.pipeline;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.RepointStrategy;

/**
 * A pipe which modifies a hex value, through usage of a HexFieldIterator
 */
public interface WritePipe {
    /**
     * Modify a HexFieldIterator using an object.
     * @param iterator The iterator to read from
     * @param object The object to modify
     * @param repointStrategy The repoint strategy to use during writing, if necessary
     * @param pipeline The pipeline being run through
     */
    void write(HexFieldIterator iterator, Object object, RepointStrategy repointStrategy, LinearPipeline pipeline);
}
