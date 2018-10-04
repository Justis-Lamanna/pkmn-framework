package com.github.lucbui.bytes;

import com.github.lucbui.file.HexFieldIterator;

import java.nio.ByteBuffer;

/**
 * Various utilities for working with bytes.
 */
public class HexUtils {

    private HexUtils(){
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

    /**
     * Easy method for creating ByteBuffers
     * @param bitesAsInts
     * @return
     */
    public static ByteBuffer toByteBuffer(int... bitesAsInts){
        byte[] bites = new byte[bitesAsInts.length];
        for(int idx = 0; idx < bitesAsInts.length; idx++){
            bites[idx] = (byte)bitesAsInts[idx];
        }
        return ByteBuffer.wrap(bites);
    }
}
