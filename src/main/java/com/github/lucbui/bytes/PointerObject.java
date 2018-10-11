package com.github.lucbui.bytes;

import com.github.lucbui.file.Pointer;

/**
 * A class which encapsulates an object, and a pointer to that object.
 * @param <P> The Pointer class
 * @param <O> The Object class
 */
public final class PointerObject <P extends Pointer, O> {

    private P pointer;
    private O object;

    public PointerObject(P pointer, O object) {
        this.pointer = pointer;
        this.object = object;
    }

    /**
     * Get the pointer to the object.
     * @return
     */
    public P getPointer() {
        return pointer;
    }

    /**
     * Get the object pointed to.
     * @return
     */
    public O getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "PointerObject{" +
                "pointer=" + pointer +
                ", object=" + object +
                '}';
    }
}
