package com.github.lucbui.utility;

import com.github.lucbui.bytes.ByteWindow;

import java.nio.ByteBuffer;
import java.util.List;

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
     * Easy method to create ByteWindows
     * @param bitesAsInts
     * @return
     */
    public static ByteWindow toByteWindow(int... bitesAsInts){
        ByteWindow bw = new ByteWindow();
        for(int idx = 0; idx < bitesAsInts.length; idx++){
            bw.set(idx, (byte)bitesAsInts[idx]);
        }
        return bw;
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

    /**
     * Easy method to create ByteWindows
     * @param bitesAsInts
     * @return
     */
    public static ByteWindow toByteWindow(long... bitesAsInts){
        ByteWindow bw = new ByteWindow();
        for(int idx = 0; idx < bitesAsInts.length; idx++){
            bw.set(idx, (byte)bitesAsInts[idx]);
        }
        return bw;
    }

    @Deprecated
    public static long fromByteBufferToLong(ByteBuffer bb) {
        if(bb.capacity() > 4){
            throw new IllegalArgumentException("Long overflow with bytebuffer > 2");
        }
        byte[] bites = bb.array();
        long value = 0;
        for(int idx = 0; idx < bites.length; idx++){
            value += (byteToUnsignedByte(bites[idx]) * (0x100L * idx));
        }
        return value;
    }

    @Deprecated
    public static int fromByteBufferToInt(ByteBuffer bb) {
        if(bb.capacity() > 2){
            throw new IllegalArgumentException("Integer overflow with bytebuffer > 2");
        }
        byte[] bites = bb.array();
        int value = 0;
        for(int idx = 0; idx < bites.length; idx++){
            value += (byteToUnsignedByte(bites[idx]) * (0x100 * idx));
        }
        return value;
    }
}
