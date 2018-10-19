package com.github.lucbui.bytes;

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
     * @param bite The byte to convert
     * @return The unsigned version of this byte.
     */
    public static int byteToUnsignedByte(byte bite){
        //We mask the first eight bites, and clear out the sign bit.
        return bite & 0xFF;
    }

    /**
     * Convert unsigned byte to its byte version
     * @param unsignedByte The byte to convert.
     * @return The signed version of this byte.
     */
    public static byte unsignedByteToByte(int unsignedByte){
        return (byte)unsignedByte;
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

    /**
     * Easy method for creating ByteBuffers
     * @param bitesAsLongs
     * @return
     */
    public static ByteBuffer toByteBuffer(long... bitesAsLongs){
        byte[] bites = new byte[bitesAsLongs.length];
        for(int idx = 0; idx < bitesAsLongs.length; idx++){
            bites[idx] = (byte)bitesAsLongs[idx];
        }
        return ByteBuffer.wrap(bites);
    }
}
