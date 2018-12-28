package com.github.lucbui.framework;

import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.file.Pointer;

import java.util.OptionalInt;

/**
 * A strategy that describes how to repoint data in case of write.
 */
public interface RepointStrategy {

    /**
     * Determine where and how to repoint data, if it needs to be repointed.
     * @param metadata Object describing the state of the object to repoint.
     * @return The position to repoint to.
     */
    Pointer repoint(RepointMetadata metadata);
}
