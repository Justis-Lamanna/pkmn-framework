package com.github.lucbui.bytes;

import com.github.lucbui.annotations.DataStructure;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an Unsigned Byte.
 *
 * Unsigned bytes are immutable. Additionally, they are unique, so all UnsignedBytes of the
 * same value are the same object.
 */
@DataStructure(size = 1)
public class UnsignedByte implements ByteObject<UnsignedByte>, Comparable<UnsignedByte>{

    /**
     * Dedicated HexReader for UnsignedByte
     */
    public static final HexReader<UnsignedByte> HEX_READER = iterator -> UnsignedByte.valueOf(iterator.get(1));

    /**
     * Dedicated HexWriter for UnsignedByte
     */
    public static final HexWriter<UnsignedByte> HEX_WRITER = (object, iterator) -> iterator.write(object.toBytes());

    //The value inside this byte.
    int value;

    //Cache results to allow for == comparison.
    private static final Map<Integer, UnsignedByte> bytes = new HashMap<>();

    private UnsignedByte(int value) {
        this.value = value;
    }

    /**
     * Parse an UnsignedByte from a literal value.
     * @param value
     * @return
     * @throws IndexOutOfBoundsException ByteBuffer has capacity smaller than 1.
     * @throws NullPointerException value is null.
     */
    public static UnsignedByte valueOf(ByteBuffer value){
        Objects.requireNonNull(value);
        if(value.capacity() < 1){
            throw new IndexOutOfBoundsException("ByteBuffer capacity < 1");
        }
        return bytes.computeIfAbsent(HexUtils.byteToUnsignedByte(value.get()), UnsignedByte::new);
    }

    /**
     * Parse an UnsignedByte from a literal value.
     * @param value
     * @return
     * @throws IllegalArgumentException Provided value is not between 0 and 255, inclusively.
     */
    public static UnsignedByte valueOf(int value){
        HexUtils.assertRange(value, 0, 0xFF);
        return bytes.computeIfAbsent(value, UnsignedByte::new);
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public UnsignedByte newInstance(long value){
        return UnsignedByte.valueOf((int) value);
    }

    /**
     * Convert this UnsignedByte to a byte representation.
     * @return
     */
    public ByteBuffer toBytes(){
        return HexUtils.toByteBuffer(value);
    }

    @Override
    public String toString() {
        return "UnsignedByte{" +
                "value=" + value +
                '}';
    }

    @Override
    public int compareTo(UnsignedByte o) {
        if(o == null){
            throw new NullPointerException("Null provided");
        }
        return this.value - o.value;
    }

    /**
     * Checks for equality with another UnsignedByte.
     * UnsignedBytes are unique, so they can be compared with the == operator as well as this operator.
     * If you want to compare two different UnsignedValues, use the {@code equalsByValue} operator.
     * @param obj The object to compare to.
     * @return True if the objects values are the same.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        } else if(this == obj){
            return true;
        } else if(obj instanceof UnsignedByte) {
            return this.value == ((UnsignedByte) obj).value;
        }
        return false;
    }

    /**
     * Checks for equality amongst other Unsigned types.
     * This allows equality checks between UnsignedByte and either UnsignedShort
     * and UnsignedWord. Essentially,
     * <code>
     *  UnsignedByte.valueOf(0).equalsByValue(UnsignedShort.valueOf(0)) == true;
     * </code>
     * @param obj The object to compare to.
     * @return True if the objects values are the same
     * @throws ClassCastException Obj cannot be cast to an UnsignedByte, UnsignedShort, or UnsignedWord.
     */
    public boolean equalsByValue(Object obj){
        if (obj == null) {
            return false;
        }if (equals(obj)) {
            return true;
        } else if(obj instanceof UnsignedShort){
            return this.value == ((UnsignedShort) obj).value;
        } else if(obj instanceof UnsignedWord){
            return this.value ==((UnsignedWord) obj).value;
        } else {
            throw new ClassCastException("Obj is type " + obj.getClass().getName() + " and can't be converted to an Unsigned type.");
        }
    }
}
