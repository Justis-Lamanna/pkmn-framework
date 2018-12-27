package com.github.lucbui.gba;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.bytes.*;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.utility.HexUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Objects;

/**
 * A GBA-style pointer.
 * A GBA Pointer is four bytes long, with the MSB indicating which memory field it should come from. The remaining
 * three bytes indicate location in that memory address. Various validation checks also are used for each memory field,
 * since not all memory can be accessed in a given field. (To avoid validation, use valueOfNoRangeCheck()).
 */
@DataStructure(size = 4)
public class GBAPointer implements Pointer, Comparable<GBAPointer>, Serializable {

    static final long serialVersionUID = 42L;

    /**
     * A Hexer which can read out a GBAPointer
     */
    public static final Hexer<GBAPointer> HEXER = new Hexer<GBAPointer>() {
        @Override
        public int getSize(GBAPointer object) {
            return 4;
        }

        @Override
        public GBAPointer read(HexFieldIterator iterator) {
            return GBAPointer.valueOf(iterator.get(4));
        }

        @Override
        public void write(GBAPointer object, HexFieldIterator iterator) {
            iterator.write(object.toByteWindow());
        }
    };

    private final Type type;
    private final long position;

    /**
     * A comparator which compares GBAPointers
     */
    public static final Comparator<GBAPointer> COMPARATOR =
            Comparator.comparing(GBAPointer::getType).thenComparingLong(GBAPointer::getLocation);

    private GBAPointer(Type type, long position){
        this.type = type;
        this.position = position;
    }

    /**
     * Create a pointer to GBA read-only memory
     * @param position The position in memory to read from
     * @return The created pointer.
     */
    public static GBAPointer valueOf(long position){
        return valueOf(Type.ROM, position);
    }

    /**
     * Get a pointer from literal values.
     * @param type The type of memory address
     * @param position The position in that address
     * @return The created pointer.
     * @throws NullPointerException Type is null
     * @throws IllegalArgumentException Position is not valid for the GBA.
     */
    public static GBAPointer valueOf(Type type, long position){
        Objects.requireNonNull(type);
        if(position < type.getStartRange() || position > type.getEndRange()){
            throw new IllegalArgumentException("Invalid pointer specified: " + position + " must be between " + type.getStartRange() + " and " + type.getEndRange());
        }
        return new GBAPointer(type, position);
    }

    /**
     * Get a pointer from raw bytes.
     * @param bytes The bytes to retrieve.
     * @return The created pointer.
     * @throws NullPointerException Type is null
     * @throws IllegalArgumentException Position is not valid for the GBA.
     * @throws IndexOutOfBoundsException ByteBuffer capacity is less than 4
     * @deprecated Not using ByteBuffer anymore
     */
    @Deprecated
    public static GBAPointer valueOf(ByteBuffer bytes){
        if(bytes.capacity() < 4){
            throw new IndexOutOfBoundsException("Bytebuffer capacity < 4");
        }
        Type type = Type.getTypeForPrefix(HexUtils.byteToUnsignedByte(bytes.get(3)));
        long value = HexUtils.byteToUnsignedByte(bytes.get(2)) * 0x10000L + HexUtils.byteToUnsignedByte(bytes.get(1)) * 0x100 + HexUtils.byteToUnsignedByte(bytes.get(0));
        return valueOf(type, value);
    }

    /**
     * Get a pointer from raw bytes
     * @param bytes The bytes to retrieve
     * @return
     */
    public static GBAPointer valueOf(ByteWindow bytes){
        Type type = Type.getTypeForPrefix(HexUtils.byteToUnsignedByte(bytes.get(3)));
        long value = HexUtils.byteToUnsignedByte(bytes.get(2)) * 0x10000L + HexUtils.byteToUnsignedByte(bytes.get(1)) * 0x100 + HexUtils.byteToUnsignedByte(bytes.get(0));
        return valueOf(type, value);
    }

    /**
     * Get the type of pointer this is.
     * @return
     */
    public Type getType(){
        return this.type;
    }

    @Override
    public long getLocation() {
        return position;
    }

    @Deprecated
    public ByteBuffer toBytes(){
        return HexUtils.toByteBuffer(position, position >> 8, position >> 16, type.getPrefix());
    }

    /**
     * Get Pointer as a ByteWindow.
     * @return
     */
    public ByteWindow toByteWindow(){
        return HexUtils.toByteWindow(position, position >> 8, position >> 16, type.getPrefix());
    }

    /**
     * Returns a pointer that points d bytes further.
     * @param d The number of bytes to add to this one.
     * @return The new GBAPointer.
     * @throws IllegalArgumentException d causes the new pointer to exceed the available range of the type.
     */
    public GBAPointer add(int d){
        return GBAPointer.valueOf(type, position + d);
    }

    @Override
    public String toString() {
        return "GBAPointer{" +
                "type=" + type +
                ", position=" + position +
                '}';
    }

    @Override
    public int compareTo(GBAPointer o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        } else if(this == obj){
            return true;
        } else if(obj instanceof GBAPointer) {
            return this.type == ((GBAPointer) obj).type && this.position == ((GBAPointer) obj).position;
        }
        return false;
    }

    /**
     * The types of memory addresses in the GBA.
     */
    public enum Type {
        /**
         * BIOS Memory, valid from 0x0 to 0x3FFF
         */
        BIOS(0x00, 0x000000, 0x003FFF),
        /**
         * On-board RAM, valid from 0x0 to 0x3FFF
         */
        WRAM_ON_BOARD(0x02, 0x000000, 0x003FFF),
        /**
         * On-chip RAM, valid from 0x to 0x7FFF
         */
        WRAM_ON_CHIP(0x03, 0x000000, 0x007FFF),
        /**
         * IO RAM, valid from 0x0 to 0x3FE
         */
        IO(0x04, 0x000000, 0x0003FE),
        /**
         * GBAPalette RAM, valid from 0x0 to 0x3FF
         */
        PALETTE_RAM(0x05, 0x000000, 0x0003FF),
        /**
         * Video RAM, valid from 0x0 to 0x17FFF
         */
        VRAM(0x06, 0x000000, 0x017FFF),
        /**
         * Object RAM, Valid from 0x0 to 0x3FF
         */
        OAM(0x07, 0x000000, 0x0003FF),
        /**
         * Read-Only Memory, valid from 0x0 to 0x1FFFFFF
         */
        ROM(0x08, 0x000000, 0x1FFFFFF),
        /**
         * Save-Only RAM, valid from 0x0 to 0xFFFF
         */
        SRAM(0x0E, 0x000000, 0x00FFFF);

        private final long prefix;

        private final long startRange;
        private final long endRange;

        Type(long prefix, long startRange, long endRange) {
            this.prefix = prefix;
            this.startRange = startRange;
            this.endRange = endRange;
        }

        /**
         * Get the prefix size.
         * @return The first byte in the four byte pointer, which represents the type of memory.
         */
        public long getPrefix() {
            return prefix;
        }

        /**
         * Get the lowest location in this type of memory.
         * @return
         */
        public long getStartRange() {
            return startRange;
        }

        /**
         * Gets the highest location in this type of memory.
         * @return
         */
        public long getEndRange() {
            return endRange;
        }

        /**
         * Gets a type from a prefix.
         * @param prefix The prefix to search.
         * @return The found prefix.
         * @throws IllegalArgumentException Prefix did not match anything registered
         */
        public static Type getTypeForPrefix(long prefix){
            for(Type type : Type.values()){
                if(type.prefix == prefix){
                    return type;
                }
            }
            throw new IllegalArgumentException("Prefix " + prefix + " does not match a GBA Memory type");
        }
    }
}
