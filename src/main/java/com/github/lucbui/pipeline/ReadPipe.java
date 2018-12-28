package com.github.lucbui.pipeline;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;

/**
 * A pipe which modifies an object, through usage of a HexFieldIterator
 */
public interface ReadPipe {
    /**
     * Modify an instance of an object, using a HexFieldIterator
     * @param object The object to modify
     * @param iterator The iterator to read from
     */
    void read(Object object, HexFieldIterator iterator, PkmnFramework pkmnFramework);
}
