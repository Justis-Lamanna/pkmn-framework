package com.github.lucbui.config;

import java.util.Objects;
import java.util.function.Function;

/**
 * An interface which all Configuration options supply.
 */
public interface Configuration {

    /**
     * Get a size for a key
     * @param key The key to retrieve.
     * @return The size
     */
    String get(String key);

    /**
     * Test if this configuration has a size for the specified key.
     * @param key The key to test for.
     * @return True if the key is present.
     */
    boolean has(String key);

    /**
     * Get a size for a key, as a string.
     * @param key The key to retrieve.
     * @param def The default size.
     * @return The size, as a string.
     */
    default String get(String key, String def){
        return has(key) ? get(key) : def;
    }

    /**
     * Get a size for a key, as a byte.
     * @param key The key to retrieve.
     * @param def The default size.
     * @return The size, as a byte.
     */
    default byte get(String key, byte def){
        return has(key) ? Byte.parseByte(get(key)) : def;
    }

    /**
     * Get a size for a key, as a short.
     * @param key The key to retrieve.
     * @param def The default size.
     * @return The size, as a short.
     */
    default short get(String key, short def){
        return has(key) ? Short.parseShort(get(key)) : def;
    }

    /**
     * Get a size for a key, as an int.
     * @param key The key to retrieve.
     * @param def The default size.
     * @return The size, as an int.
     */
    default int get(String key, int def){
        return has(key) ? Integer.parseInt(get(key)) : def;
    }

    /**
     * Get a size for a key, as a long.
     * @param key The key to retrieve.
     * @param def The default size.
     * @return The size, as a long.
     */
    default long get(String key, long def){
        return has(key) ? Long.parseLong(get(key)) : def;
    }

    /**
     * Get a size for a key, as a float.
     * @param key The key to retrieve.
     * @param def The default size.
     * @return The size, as a float.
     */
    default float get(String key, float def){
        return has(key) ? Float.parseFloat(get(key)) : def;
    }

    /**
     * Get a size for a key, as a double.
     * @param key The key to retrieve.
     * @param def The default size.
     * @return The size, as a double.
     */
    default double get(String key, double def){
        return has(key) ? Double.parseDouble(get(key)) : def;
    }

    /**
     * Get a size for a key, as an object.
     * @param key The key to retrieve.
     * @param converter A function that converts a string into its final size.
     * @param def The default size.
     * @return The size, as a byte.
     */
    default <T> T get(String key, Function<String, T> converter, T def){
        Objects.requireNonNull(converter, "Null converter specified");
        return has(key) ? converter.apply(get(key)) : def;
    }
}
