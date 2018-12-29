package com.github.lucbui.pipeline;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;

/**
 * A pipe which modifies a hex value, through usage of a HexFieldIterator
 */
public interface WritePipe {
    /**
     * Modify a HexFieldIterator using an object.
     * @param iterator The iterator to read from
     * @param object The object to modify
     */
    void write(HexFieldIterator iterator, Object object, PkmnFramework pkmnFramework);
}
