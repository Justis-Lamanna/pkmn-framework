package com.github.lucbui.bytes;

import com.github.lucbui.utility.MathUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A byte where each bit can hold one of three states
 */
public class TribitByte {

    /**
     * The number of Tribits in a Tribyte
     */
    public static final int LENGTH = 8;

    /**
     * The value stored. value[0] contains the MSB, and value[7] the LSB.
     */
    private Tribit[] value;

    /**
     * Set to true if a dont care condition is in this TribitByte.
     */
    private boolean hasDontCare;

    /**
     * A TribitByte filled with DONT_CARES
     */
    public static TribitByte DONT_CARE = new TribitByte(new Tribit[]{null, null, null, null, null, null, null, null});

    /**
     * A TribitByte filled with 0s
     */
    public static TribitByte ALL_ZERO = value(0b00000000);

    /**
     * A TribitByte filled with 1s
     */
    public static TribitByte ALL_ONE = value(0b11111111);

    private TribitByte(Tribit[] value){
        this.value = value;
        this.hasDontCare = Arrays.stream(value).anyMatch(i -> i == Tribit.DONT_CARE);
    }

    /**
     * Get a copy of a TribitByte
     * @param bite The byte to copy
     * @return A copy of this TribitByte
     */
    public static TribitByte value(TribitByte bite){
        Objects.requireNonNull(bite);
        return new TribitByte(Arrays.copyOf(bite.value, bite.value.length));
    }

    /**
     * Create a TribitByte from an array of integers.
     * 8 bits must be supplied.
     * Bits should be 0 to represent Tribit.ZERO, 1 to represent Tribit.ONE, and null to represent Tribit.DONT_CARE.
     * Use of any other integers results in an error.
     * @param bits
     * @return
     */
    public static TribitByte value(Integer... bits){
        if(bits.length != LENGTH){
            throw new IllegalArgumentException("bits must be size of " + LENGTH);
        }
        Arrays.stream(bits).forEach(i -> {
            if(i != null && i != 0 && i != 1){
                throw new IllegalArgumentException("Bits must be 0, 1, or null. " + i + " found.");
            }
        });
        Tribit[] mappedBits = Arrays.stream(bits).map(i -> {
            if(i == null){
                return Tribit.DONT_CARE;
            } else if(i == 0){
                return Tribit.ZERO;
            } else {
                return Tribit.ONE;
            }
        }).toArray(Tribit[]::new);
        return new TribitByte(mappedBits);
    }

    /**
     * Convert an array of Tribits into a TribitByte.
     * The supplied array must be 8 bytes long, and free of nulls.
     * @param bits An array of Tribits to use in the value
     * @return A TribitByte encapsulating the supplied bits
     */
    public static TribitByte value(Tribit... bits){
        if(bits.length != LENGTH){
            throw new IllegalArgumentException("bits must be size of " + LENGTH);
        }
        Arrays.stream(bits).forEach(Objects::requireNonNull);
        return new TribitByte(Arrays.copyOf(bits, bits.length));
    }

    /**
     * Convert a byte value to a TribitByte
     * @param realByte The byte to convert
     * @return A TribitByte representing that value
     */
    public static TribitByte value(int realByte){
        Tribit[] bits = new Tribit[LENGTH];
        int current = realByte;
        for(int idx = 0; idx < LENGTH; idx++){
           int bit = current & 0b1;
           bits[idx] = (bit == 0 ? Tribit.ZERO : Tribit.ONE);
           current = current >>> 1;
        }
        return new TribitByte(bits);
    }

    /**
     * Get the bit at the specified position
     * @param bit The bit to get. 0 is the LSB.
     * @return The bit
     */
    public Tribit getBit(int bit){
        MathUtils.assertInRange(bit, 0, LENGTH - 1);
        return value[bit];
    }

    /**
     * Set the bit at the specified position
     * @param bitPosition The bit to modify. 0 is the LSB.
     * @param bit The bit to replace it with
     * @return A new TribitByte that is a copy of this one, with the specified bit set.
     */
    public TribitByte setBit(int bitPosition, Tribit bit){
        MathUtils.assertInRange(bitPosition, 0, LENGTH - 1);
        Objects.requireNonNull(bit);
        Tribit[] newValues = Arrays.copyOf(this.value, this.value.length);
        newValues[bitPosition] = bit;
        return new TribitByte(newValues);
    }

    /**
     * Get the bits of this TribitByte
     * @return The bits, with the first position being the MSB, and last being the LSB.
     */
    public Tribit[] getBits(){
        return Arrays.copyOf(this.value, this.value.length);
    }

    /**
     * Converts the TribitByte into a number byte
     * @param dontCareValue The bit (0 or 1) to use when encountering a Dont Care
     * @return A byte representing the given TribitByte
     */
    public int toByte(int dontCareValue){
        if(dontCareValue != 0 && dontCareValue != 1){
            throw new IllegalArgumentException("dontCareValue must be 0 or 1");
        }
        int bite = 0;
        for(int idx = LENGTH - 1; idx >= 0; idx--){
            int bit;
            switch(value[idx]){
                case ZERO: bit = 0; break;
                case ONE: bit = 1; break;
                default: bit = dontCareValue; break;
            }
            bite |= bit;
            bite <<= 1;
        }
        //During conversion, we shifted one too many.
        return bite >> 1;
    }

    /**
     * Converts the TribitByte into a number byte, using 0 for DONT_CAREs
     * @return A byte representing the given TribitByte
     */
    public int toByte(){
        return toByte(0);
    }

    /**
     * Logical and this byte with another
     * @param other The other TribitByte
     * @return The logical and
     */
    public TribitByte and(TribitByte other){
        Objects.requireNonNull(other);
        Tribit[] and = Stream.iterate(0, i -> i + 1)
                .limit(LENGTH)
                .map(i -> Tribit.and(value[i], other.value[i]))
                .toArray(Tribit[]::new);
        return new TribitByte(and);
    }

    /**
     * Logical or this byte with another
     * @param other The other TribitByte
     * @return The logical or
     */
    public TribitByte or(TribitByte other){
        Objects.requireNonNull(other);
        Tribit[] or = Stream.iterate(0, i -> i + 1)
                .limit(LENGTH)
                .map(i -> Tribit.or(value[i], other.value[i]))
                .toArray(Tribit[]::new);
        return new TribitByte(or);
    }

    /**
     * Negate this byte
     * @return The logical not
     */
    public TribitByte not(){
        Tribit[] not = Stream.iterate(0, i -> i + 1)
                .limit(LENGTH)
                .map(i -> Tribit.not(value[i]))
                .toArray(Tribit[]::new);
        return new TribitByte(not);
    }

    /**
     * Shift this right by some amount.
     * Logical shifting is performed, meaning the positions shifted out of become 0s. If a shift amount of
     * 0 is provided, this current TribitByte is returned. A negative value causes a left shift, rather than a right shift.
     *
     * Shifting by anything larger than 7 or less than -7 results in a TribitByte of all zero being returned.
     *
     * @param shift The number of bit positions to shift
     * @return A new byte that is a shift of this byte by the specified amount.
     */
    public TribitByte shiftRight(int shift){
        if(shift == 0){
            return this;
        } else if(shift < 0){
            TribitByte zeroes = TribitByte.value(0x0);
            if(LENGTH + shift > 0) {
                System.arraycopy(this.value, 0, zeroes.value, -shift, LENGTH + shift);
            }
            return zeroes;
        } else {
            TribitByte zeroes = TribitByte.value(0x0);
            if(LENGTH - shift > 0) {
                System.arraycopy(this.value, shift, zeroes.value, 0, LENGTH - shift);
            }
            return zeroes;
        }
    }

    /**
     * Checks if this TribitByte has at least one DONT_CARE condition
     * @return True if one bit of this TribitByte is DONT_CARE.
     */
    public boolean isDontCare(){
        return hasDontCare;
    }

    /**
     * Shift this left by some amount.
     * Logical shifting is performed, meaning the positions shifted out of become 0s. If a shift amount of
     * 0 is provided, this current TribitByte is returned. A negative value causes a right shift, rather than a left shift.
     *
     * Shifting by anything larger than 7 or less than -7 results in a TribitByte of all zero being returned.
     *
     * This is equivalent to the shiftRight method, with the shift parameter negated.
     *
     * @param shift The number of bit positions to shift
     * @return A new byte that is a shift of this byte by the specified amount.
     */
    public TribitByte shiftLeft(int shift){
        return shiftRight(-shift);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(24);
        for(Tribit bit : value){
            switch(bit){
                case ZERO: sb.insert(0, "0"); break;
                case ONE: sb.insert(0, "1"); break;
                case DONT_CARE: sb.insert(0, "X"); break;
            }
        }
        return sb.insert(0, "[TribitByte=0x").append("]").toString();
    }

    /**
     * Test if this TribitByte is equal to another, ignoring DONT_CARE conditions
     * For example, 0x00011X11 and 0x00011011 would be considered equal. Similarly,
     * 0x00011111 would also be considered equal.
     * @param o The other TribitByte to test
     * @return True if the objects are equals
     */
    public boolean equalsDontCare(TribitByte o){
        if (o == null) return false;
        if (this == o) return true;
        return Stream.iterate(0, i -> i + 1)
                .limit(LENGTH)
                .allMatch(idx -> {
                    Tribit thisBit = value[idx];
                    Tribit otherBit = o.value[idx];
                    if(thisBit == Tribit.DONT_CARE || otherBit == Tribit.DONT_CARE){
                        return true;
                    } else {
                        return thisBit == otherBit;
                    }
                });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TribitByte that = (TribitByte) o;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    /**
     * Modify this TribitByte
     * @return A builder pre-populated with this TriByte's contents
     */
    public Builder modify(){
        return new Builder(this);
    }

    /**
     * Create a TribitByte.
     * The fillBit describes the initial bit in each position.
     * @param fillBit The "seed" bit.
     * @return A builder to create a TribitByte
     */
    public static Builder create(Tribit fillBit){
        return new Builder(fillBit);
    }

    /**
     * Create a TribitByte
     * The Builder is initialized with all ZERO bits
     * @return A builder to create a TribitByte
     */
    public static Builder create(){
        return new Builder(Tribit.ZERO);
    }

    /**
     * Builder to make or edit a TribitByte
     */
    public static class Builder {
        private int cursor;
        private Tribit[] bits;

        private Builder(TribitByte bite){
            Objects.requireNonNull(bite);
            this.bits = Arrays.copyOf(bite.value, bite.value.length);
            this.cursor = 0;
        }

        private Builder(Tribit initialBit){
            Objects.requireNonNull(initialBit);
            this.bits = new Tribit[LENGTH];
            Arrays.fill(bits, initialBit);
            this.cursor = 0;
        }

        /**
         * Set the next bit to a certain value.
         * The cursor is incremented to the next value in the set.
         * @param bit The bit to set
         * @return This builder
         */
        public Builder then(Tribit bit){
            Objects.requireNonNull(bit);
            MathUtils.assertInRange(this.cursor, 0, LENGTH - 1);
            bits[this.cursor] = bit;
            this.cursor += 1;
            return this;
        }

        /**
         * Move the cursor to a hard-coded position
         * @param cursor The cursor position
         * @return This builder
         */
        public Builder to(int cursor){
            MathUtils.assertInRange(cursor, 0, LENGTH - 1);
            this.cursor = cursor;
            return this;
        }

        /**
         * Set a specific position to a certain bit.
         * The cursor is set to the next position in the bit.
         * @param position The position
         * @param bit The bit
         * @return This builder
         */
        public Builder set(int position, Tribit bit){
            Objects.requireNonNull(bit);
            MathUtils.assertInRange(position, 0, LENGTH - 1);
            bits[position] = bit;
            this.cursor = position + 1;
            return this;
        }

        /**
         * Create the TribitByte
         * @return The created TribitByte
         */
        public TribitByte build(){
            return new TribitByte(bits);
        }
    }
}
