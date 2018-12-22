package com.github.lucbui.utility;

import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Utilities for parsing
 */
public class ParseUtils {

    /**
     * Parse a long, without throwing an exception
     * @param value The value to parse
     * @param radix The radix to use
     * @return The parsed value, or an empty optional if it failed.
     */
    public static OptionalLong parseLong(String value, int radix){
        try{
            return OptionalLong.of(Long.parseLong(value, radix));
        } catch (NumberFormatException ex){
            return OptionalLong.empty();
        }
    }

    /**
     * Parse an integer, without throwing an exception
     * @param value The value to parse
     * @param radix The radix to use
     * @return The parsed value, or an empty optional if it failed.
     */
    public static OptionalInt parseInt(String value, int radix){
        try{
            return OptionalInt.of(Integer.parseInt(value, radix));
        } catch (NumberFormatException ex){
            return OptionalInt.empty();
        }
    }
}
