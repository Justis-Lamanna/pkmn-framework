package com.github.lucbui.config;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * An interface which all Configuration options supply.
 */
public interface Configuration {

    /**
     * Get a value for a key
     * @param key The key to retrieve.
     * @return The value
     */
    Optional<String> get(String key);

    /**
     * Test if this configuration has a value for the specified key.
     * @param key The key to test for.
     * @return True if the key is present.
     */
    boolean has(String key);

    /**
     * Get a value for a key, as an object.
     * @param key The key to retrieve.
     * @param converter A function that converts a string into its final value.
     * @return The value, as a byte.
     */
    default <T> Optional<T> get(String key, Function<String, T> converter){
        Objects.requireNonNull(converter, "Null converter specified");
        return get(key).map(converter);
    }
}
