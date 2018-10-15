package com.github.lucbui.config;

import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Function;

/**
 * A configuration that can be modified and saved.
 */
public interface MutableConfig extends Configuration {

    /**
     * Save the configuration to an output
     * @param out The output.
     */
    void save(OutputStream out);

    /**
     * Set a key/value pair in the configuration.
     * @param key The key
     * @param value The value
     */
    void set(String key, String value);

    /**
     * Set a key/value pair in the configuration.
     * @param key The key
     * @param value The value
     */
    default void set(String key, byte value){
        set(key, Byte.toString(value));
    }

    /**
     * Set a key/value pair in the configuration.
     * @param key The key
     * @param value The value
     */
    default void set(String key, short value){
        set(key, Short.toString(value));
    }

    /**
     * Set a key/value pair in the configuration.
     * @param key The key
     * @param value The value
     */
    default void set(String key, int value){
        set(key, Integer.toString(value));
    }

    /**
     * Set a key/value pair in the configuration.
     * @param key The key
     * @param value The value
     */
    default void set(String key, long value){
        set(key, Long.toString(value));
    }

    /**
     * Set a key/value pair in the configuration.
     * @param key The key
     * @param value The value
     */
    default void set(String key, float value){
        set(key, Float.toString(value));
    }

    /**
     * Set a key/value pair in the configuration.
     * @param key The key
     * @param value The value
     */
    default void set(String key, double value){
        set(key, Double.toString(value));
    }

    /**
     * Set a key/value pair in the configuration.
     * @param key The key
     * @param value The value
     * @param converter A function that converts to value to a string.
     * @param <T> The type of the object.
     */
    default <T> void set(String key, T value, Function<T, String> converter){
        Objects.requireNonNull(converter);
        set(key, converter.apply(value));
    }
}
