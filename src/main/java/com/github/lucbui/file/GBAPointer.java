package com.github.lucbui.file;

import com.github.lucbui.bytes.ByteUtils;
import com.github.lucbui.bytes.HexReader;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * A GBA-style pointer.
 * A GBA Pointer is four bytes long, with the MSB indicating which memory field it should come from. The remaining
 * three bytes indicate location in that memory address. Various validation checks also are used for each memory field,
 * since not all memory can be accessed in a given field. (To avoid validation, use valueOfNoRangeCheck()).
 */
public class GBAPointer implements Pointer, Comparable<GBAPointer> {

    public static final HexReader<GBAPointer> HEX_READER = iterator -> GBAPointer.valueOf(iterator.get(4));

    private final Type type;
    private final long position;

    private GBAPointer(Type type, long position){
        this.type = type;
        this.position = position;
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
     */
    public static GBAPointer valueOf(ByteBuffer bytes){
        if(bytes.capacity() < 4){
            throw new IndexOutOfBoundsException("Bytebuffer capacity < 4");
        }
        Type type = Type.getTypeForPrefix(ByteUtils.byteToUnsignedByte(bytes.get(3)));
        long value = ByteUtils.byteToUnsignedByte(bytes.get(2)) * 0x10000L + ByteUtils.byteToUnsignedByte(bytes.get(1)) * 0x100 + ByteUtils.byteToUnsignedByte(bytes.get(0));
        return valueOf(type, value);
    }

    /**
     * Create a pointer from literal values, without a range check.
     * @param type The type of memory address
     * @param position The position in that address
     * @return The created pointer.
     */
    public static GBAPointer valueOfNoRangeCheck(Type type, long position){
        Objects.requireNonNull(type);
        if(position < 0){
            throw new IllegalArgumentException("Invalid pointer specified: " + position + " must be greater than zero");
        }
        return new GBAPointer(type, position);
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

    @Override
    public String toString() {
        return "GBAPointer{" +
                "type=" + type +
                ", position=" + position +
                '}';
    }

    @Override
    public int compareTo(GBAPointer o) {
        if(o == null){
            throw new NullPointerException("Null provided");
        }
        int typeDifference = this.type.ordinal() - o.type.ordinal();
        if(typeDifference == 0){
            return (int)Math.signum(this.position - o.position);
        }
        return typeDifference;
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
         * Palette RAM, valid from 0x0 to 0x3FF
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
         * Get the prefix value.
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
