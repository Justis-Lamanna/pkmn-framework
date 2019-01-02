package com.github.lucbui.strategy;

/**
 * A pipe which creates an instance of a specified class
 */
public interface CreateStrategy {

    /**
     * Create an object of the given class
     * @param clazz The class to create
     * @return A created instance of an object.
     */
    <T> T create(Class<T> clazz);
}
