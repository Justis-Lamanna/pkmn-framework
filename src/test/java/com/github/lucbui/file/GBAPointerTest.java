package com.github.lucbui.file;

import com.github.lucbui.gba.GBAPointer;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GBAPointerTest {

    @Test
    void valueOfValid(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.BIOS, 0);
        assertSame(ptr.getType(), GBAPointer.Type.BIOS);
        assertEquals(0, ptr.getLocation());
    }

    @Test
    void valueOfNullType(){
        assertThrows(NullPointerException.class, () -> GBAPointer.valueOf(null, 0));
    }

    @Test
    void valueOfInvalidPosition(){
        assertThrows(IllegalArgumentException.class, () -> GBAPointer.valueOf(GBAPointer.Type.BIOS, -1));
    }

    @Test
    void valueOfValidByteBuffer(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0, (byte)0x80, 0x08});
        GBAPointer pointer = GBAPointer.valueOf(bb);
        assertEquals(GBAPointer.Type.ROM, pointer.getType());
        assertEquals(0x800000, pointer.getLocation());
    }

    @Test
    void valueOfInvalidByteBuffer(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0, (byte)0x80, 0x03});
        assertThrows(IllegalArgumentException.class, () -> GBAPointer.valueOf(bb));
    }

    @Test
    void valueOfTooSmallByteBuffer(){
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0});
        assertThrows(IndexOutOfBoundsException.class, () -> GBAPointer.valueOf(bb));
    }

    @Test
    void equals(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        GBAPointer ptr2 = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        assertEquals(ptr, ptr2);
    }

    @Test
    void equalsDifferent(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        GBAPointer ptr2 = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800003);
        assertNotEquals(ptr, ptr2);
    }

    @Test
    void equalsNull(){
        assertNotEquals(GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000), null);
    }

    @Test
    void compareToSame(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        GBAPointer ptr2 = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        assertEquals(0, ptr.compareTo(ptr2));
    }

    @Test
    void compareToDifferentTypes(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        GBAPointer ptr2 = GBAPointer.valueOf(GBAPointer.Type.BIOS, 0x0);
        assertTrue(ptr.compareTo(ptr2) > 0);
    }

    @Test
    void compareToDifferentLocations(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800010);
        GBAPointer ptr2 = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800000);
        assertTrue(ptr.compareTo(ptr2) > 0);
    }

    @Test
    void compareToNull(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800010);
        assertThrows(NullPointerException.class, () -> ptr.compareTo(null));
    }

    @Test
    void addSmallAmount(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800010);
        GBAPointer ptr2 = ptr.add(10);
        assertEquals(0x800010 + 10, ptr2.getLocation());
    }

    @Test
    void addTooMuch(){
        GBAPointer ptr = GBAPointer.valueOf(GBAPointer.Type.ROM, 0x800010);
        assertThrows(IllegalArgumentException.class, () -> ptr.add(Integer.MAX_VALUE));
    }
}