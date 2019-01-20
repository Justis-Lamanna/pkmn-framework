package com.github.lucbui.bytes;

import com.github.lucbui.file.Pointer;
import com.github.lucbui.utility.RepointUtils;

import java.util.Objects;

/**
 * A "fat pointer" which encapsulates an object, and a pointer to that object.
 * @param <O> The Object class
 */
public final class PointerObject <O> {

    private Pointer pointer;
    private O object;
    private RepointStrategy repointStrategy;

    /**
     * Create a PointerObject
     * By default, a disabling RepointStrategy is used. Attempting to write such an object will
     * result in an exception being thrown. It is necessary to specify a
     * @param pointer The pointer to the object
     * @param object The object itself
     */
    public PointerObject(Pointer pointer, O object) {
        this.pointer = Objects.requireNonNull(pointer);
        this.object = Objects.requireNonNull(object);
        this.repointStrategy = RepointUtils.disableRepointStrategy();
    }

    /**
     * Creates a PointerObject with specified RepointStrategy
     * @param pointer The pointer to the object
     * @param object The object itself
     * @param repointStrategy The RepointStrategy to use during write.
     */
    public PointerObject(Pointer pointer, O object, RepointStrategy repointStrategy){
        this.pointer = Objects.requireNonNull(pointer);
        this.object = Objects.requireNonNull(object);
        this.repointStrategy = Objects.requireNonNull(repointStrategy);
    }

    /**
     * Get the pointer to the object.
     * @return
     */
    public Pointer getPointer() {
        return pointer;
    }

    /**
     * Get the object pointed to.
     * @return
     */
    public O getObject() {
        return object;
    }

    /**
     * Set the object pointed to
     * @param object The object that should be pointed to
     */
    public void setObject(O object){
        this.object = object;
    }

    /**
     * Get the RepointStrategy to be used for writing this object.
     * @return
     */
    public RepointStrategy getRepointStrategy() {
        return repointStrategy;
    }

    /**
     * Set the RepointStrategy to be used for writing this object.
     * @param repointStrategy The repointStrategy to use.
     */
    public void setRepointStrategy(RepointStrategy repointStrategy) {
        Objects.requireNonNull(repointStrategy);
        this.repointStrategy = repointStrategy;
    }

    @Override
    public String toString() {
        return "PointerObject{" +
                "pointer=" + pointer +
                ", object=" + object +
                '}';
    }
}
