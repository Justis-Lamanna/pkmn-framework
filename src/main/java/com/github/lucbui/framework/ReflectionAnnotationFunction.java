package com.github.lucbui.framework;

import com.github.lucbui.file.HexFieldIterator;

import java.lang.reflect.Field;

/**
 * A common interface for registering custom annotations
 */
public interface ReflectionAnnotationFunction {

    /**
     * Execute this code when a field being read.
     * @param obj The object being read into.
     * @param field The field being written.
     * @param iterator The iterator being used.
     */
    void onRead(Object obj, Field field, HexFieldIterator iterator);

    /**
     * Execute this code when a field is being written.
     * @param obj The object being written.
     * @param field The field being read.
     * @param iterator The iterator being used.
     * @param repointStrategy Repoint strategy being used.
     */
    void onWrite(Object obj, Field field, HexFieldIterator iterator, RepointStrategy repointStrategy);
}
