package com.github.lucbui.utility;

import java.util.OptionalInt;
import java.util.OptionalLong;

public class MathUtils {

    private MathUtils(){
        //Don't instantiate!
    }

    /**
     * Clamp a number between two numbers
     * If the number provided is lower than the lower bound, or higher than the higher bound,
     * the number is forced within bounds
     * @param num The number to clamp
     * @param lower The lower bound
     * @param upper The upper bound
     * @return Num, or lower if num &lt; lower, or upper if num &gt; upper
     */
    public static int clamp(int num, int lower, int upper){
        if(num < lower){
            return lower;
        } else if(num > upper){
            return upper;
        } else {
            return num;
        }
    }

    /**
     * Assert a number in a range
     * @param num The number to check
     * @param lower The lower bound
     * @param upper The upper bound
     */
    public static int assertInRange(int num, int lower, int upper){
        if(num > upper || num < lower){
            throw new IllegalArgumentException("Expected number to be between " + upper + " and " + lower + "; Found " + num);
        }
        return num;
    }

    /**
     * Assert a number is non-negative
     * @param value The value to test
     * @return Value, returned
     */
    public static long assertNonNegative(long value) {
        if(value < 0){
            throw new IllegalArgumentException("Expected non-negative number, found " + value);
        }
        return value;
    }

    /**
     * Assert a number is positive
     * @param value The value to test
     * @return Value, returned
     */
    public static long assertPositive(long value){
        if(value <= 0){
            throw new IllegalArgumentException("Expected positive number, found " + value);
        }
        return value;
    }

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
