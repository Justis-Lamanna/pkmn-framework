package com.github.lucbui.file;

import com.github.lucbui.gba.GBAPointer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class GBAPointerTest {

    @Test
    public void valueOfValid(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.BIOS, 0);
        assertSame(ptr.getType(), GBAPointer.Type.BIOS);
        assertEquals(0, ptr.getLocation());
    }

    @Test(expected = NullPointerException.class)
    public void valueOfNullType(){
        GBAPointer.valueOf(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfInvalidPosition(){
        GBAPointer.valueOf(GBAPointer.Type.BIOS, -1);
    }

    @Test
    public void valueOfValidByteBuffer(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0, (byte)0x80, 0x08});
        GBAPointer pointer = GBAPointer.valueOf(bb);
        assertEquals(GBAPointer.Type.ROM, pointer.getType());
        assertEquals(0x800000, pointer.getLocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfInvalidByteBuffer(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0, (byte)0x80, 0x03});
        GBAPointer.valueOf(bb);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void valueOfTooSmallByteBuffer(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0});
        GBAPointer.valueOf(bb);
    }

    @Test
    public void equals(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        GBAPointer ptr2 = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        assertEquals(ptr, ptr2);
    }

    @Test
    public void compareToSame(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        GBAPointer ptr2 = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        assertEquals(0, ptr.compareTo(ptr2));
    }

    @Test
    public void compareToDifferentTypes(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        GBAPointer ptr2 = GBAPointer.valueOf(GBAPointer.Type.BIOS, 0x0);
        assertTrue(ptr.compareTo(ptr2) > 0);
    }

    @Test
    public void compareToDifferentLocations(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800010);
        GBAPointer ptr2 = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        assertTrue(ptr.compareTo(ptr2) > 0);
    }

    @Test(expected = NullPointerException.class)
    public void compareToNull(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800010);
        ptr.compareTo(null);
    }

    @Test
    public void addSmallAmount(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800010);
        GBAPointer ptr2 = ptr.add(10);
        assertEquals(0x800010 + 10, ptr2.getLocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTooMuch(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800010);
        ptr.add(Integer.MAX_VALUE);
    }
}