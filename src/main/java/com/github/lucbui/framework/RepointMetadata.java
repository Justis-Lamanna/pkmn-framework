package com.github.lucbui.framework;

import com.github.lucbui.bytes.PointerObject;

import java.util.OptionalInt;

/**
 * An object describing the state needed for repointing.
 */
public class RepointMetadata {
    private PointerObject<?> pointerObject;

    public RepointMetadata(PointerObject<?> pointerObject){
        this.pointerObject = pointerObject;
    }

    public PointerObject<?> getPointerObject() {
        return pointerObject;
    }
}
