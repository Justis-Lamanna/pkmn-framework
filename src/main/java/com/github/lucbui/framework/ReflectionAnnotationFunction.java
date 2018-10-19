package com.github.lucbui.framework;

import com.github.lucbui.file.HexFieldIterator;

import java.lang.reflect.Field;

/**
 * A common interface for registering custom annotations
 */
public interface ReflectionAnnotationFunction {

    /**
     * Execute this code when a field being read.
     * @param objToReadFrom The object being read into.
     * @param field The field being written.
     * @param iterator The iterator being used.
     * @return The object to write to this field, or null if nothing happened.
     */
    Object onRead(Object objToReadFrom, Field field, HexFieldIterator iterator) throws IllegalAccessException;

    /**
     * Execute this code when a field is being written.
     * @param objToWrite The object being written.
     * @param iterator The iterator being used.
     * @return True if the object was written.
     */
    boolean onWrite(Object objToWrite, Field field, HexFieldIterator iterator) throws IllegalAccessException;
}
