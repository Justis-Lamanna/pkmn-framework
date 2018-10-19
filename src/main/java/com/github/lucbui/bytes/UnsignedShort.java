package com.github.lucbui.bytes;

import com.github.lucbui.annotations.DataStructure;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an UnsignedShort
 *
 * UnsignedShorts are immutable. Additionally, they are unique, so all UnsignedShorts of the
 * same size are the same object.
 */
@DataStructure(size = 2)
public class UnsignedShort implements ByteObject<UnsignedShort>, Comparable<UnsignedShort> {

    /**
     * Dedicated HexReader for UnsignedShort
     */
    public static final HexReader<UnsignedShort> HEX_READER = iterator -> UnsignedShort.valueOf(iterator.get(2));

    /**
     * Dedicated HexWriter for UnsignedShort
     */
    public static final HexWriter<UnsignedShort> HEX_WRITER = (object, iterator) -> iterator.write(object.toBytes());

    //The size inside this short.
    int value;

    //Cache results to allow for == comparison.
    private static final Map<Integer, UnsignedShort> shorts = new HashMap<>();

    private UnsignedShort(int value) {
        this.value = value;
    }

    /**
     * Parse an UnsignedByte from a literal bytestring.
     * @param bytes
     * @return
     * @throws IndexOutOfBoundsException ByteBuffer has capacity smaller than 2.
     * @throws NullPointerException size is null.
     */
    public static UnsignedShort valueOf(ByteBuffer bytes){
        Objects.requireNonNull(bytes);
        if(bytes.capacity() < 2){
            throw new IndexOutOfBoundsException("ByteBuffer capacity < 2");
        }
        int value = HexUtils.byteToUnsignedByte(bytes.get(1)) * 0x100 + HexUtils.byteToUnsignedByte(bytes.get(0));
        return shorts.computeIfAbsent(value, UnsignedShort::new);
    }

    /**
     * Parse an UnsignedByte from a literal size.
     * @param value
     * @return
     * @throws IllegalArgumentException Provided size is not between 0 and 0xFFFF, inclusively.
     */
    public static UnsignedShort valueOf(int value){
        HexUtils.assertRange(value, 0, 0xFFFF);
        return shorts.computeIfAbsent(value, UnsignedShort::new);
    }

    /**
     * Upcast an UnsignedByte to an UnsignedShort
     * @param uByte
     * @return
     * @throws NullPointerException uByte is null.
     */
    public static UnsignedShort valueOf(UnsignedByte uByte){
        Objects.requireNonNull(uByte);
        return shorts.computeIfAbsent(uByte.value, UnsignedShort::new);
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public UnsignedShort newInstance(long value){
        return UnsignedShort.valueOf((int) value);
    }

    public ByteBuffer toBytes() {
        //We can do this, because casting to a byte chops off everything except the 8 LSBs
        return HexUtils.toByteBuffer(value, value >>> 8);
    }

    @Override
    public String toString() {
        return "UnsignedShort{" +
                "size=" + value +
                '}';
    }

    @Override
    public int compareTo(UnsignedShort o) {
        if(o == null){
            throw new NullPointerException("Null provided");
        }
        return this.value - o.value;
    }

    /**
     * Checks for equality with another UnsignedShort.
     * UnsignedShorts are unique, so they can be compared with the == operator as well as this operator.
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
        } else if(obj instanceof UnsignedShort) {
            return this.value == ((UnsignedShort) obj).value;
        }
        return false;
    }

    /**
     * Checks for equality amongst other Unsigned types.
     * This allows equality checks between UnsignedShort and either UnsignedByte
     * and UnsignedWord. Essentially,
     * <code>
     *  UnsignedShort.valueOf(0).equalsByValue(UnsignedByte.valueOf(0)) == true;
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
        } else if(obj instanceof UnsignedByte){
            return this.value == ((UnsignedByte) obj).value;
        } else if(obj instanceof UnsignedWord){
            return this.value ==((UnsignedWord) obj).value;
        } else {
            throw new ClassCastException("Obj is type " + obj.getClass().getName() + " and can't be converted to an Unsigned type.");
        }
    }
}
