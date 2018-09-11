package com.github.lucbui.bytes;

import com.github.lucbui.file.HexFieldIterator;

/**
 * A common interface for byte-object translation.
 * @param <T> The type of object being translated into.
 */
public interface HexReader<T> {

    /**
     * Translates a sequence of bytes into an object.
     * @param iterator The iterator to translate from.
     * @return The created object.
     */
    T translate(HexFieldIterator iterator);
}
