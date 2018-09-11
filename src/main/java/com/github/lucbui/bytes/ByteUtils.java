package com.github.lucbui.bytes;

/**
 * Various utilities for working with bytes.
 */
public class ByteUtils {

    private ByteUtils(){
        //Nothin
    }

    /**
     * Check if a value is between two numbers.
     * @param value The value to check.
     * @param lowest The lowest the value can be.
     * @param highest The highest the value can be.
     */
    public static void assertRange(long value, long lowest, long highest){
        if(lowest > highest){
            throw new IllegalArgumentException("Lowest must be less than highest");
        }
        if(value < lowest || value > highest){
            throw new IllegalArgumentException("value " + value + " must be between " + lowest + " and " + highest);
        }
    }

    /**
     * Converts a byte to its unsigned version (as an integer)
     * @param bite The bite to convert
     * @return The unsigned version of this bite.
     */
    public static int byteToUnsignedByte(byte bite){
        //We mask the first eight bites, and clear out the sign bit.
        return bite & 0xFF;
    }
}
