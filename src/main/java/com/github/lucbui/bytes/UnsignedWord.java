package com.github.lucbui.bytes;

import com.github.lucbui.annotations.DataStructure;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@DataStructure(size = 4)
public class UnsignedWord implements ByteObject<UnsignedWord>,Comparable<UnsignedWord> {

    /**
     * Dedicated HexReader for UnsignedWord
     */
    public static final HexReader<UnsignedWord> HEX_READER = iterator -> UnsignedWord.valueOf(iterator.get(4));

    /**
     * Dedicated HexWriter for UnsignedWord
     */
    public static final HexWriter<UnsignedWord> HEX_WRITER = (object, iterator) -> iterator.write(object.toBytes());

    //The value inside this short.
    long value;

    //Cache results to allow for == comparison.
    private static final Map<Long, UnsignedWord> words = new HashMap<>();

    private UnsignedWord(long value){
        this.value = value;
    }

    /**
     * Parse an UnsignedWord from a literal bytestring.
     * @param bytes
     * @return
     * @throws IndexOutOfBoundsException ByteBuffer has capacity smaller than 4.
     * @throws NullPointerException value is null.
     */
    public static UnsignedWord valueOf(ByteBuffer bytes){
        Objects.requireNonNull(bytes);
        if(bytes.capacity() < 4){
            throw new IndexOutOfBoundsException("ByteBuffer capacity < 4");
        }
        long value = HexUtils.byteToUnsignedByte(bytes.get(3)) * 0x1000000L + HexUtils.byteToUnsignedByte(bytes.get(2)) * 0x10000 + HexUtils.byteToUnsignedByte(bytes.get(1)) * 0x100 + HexUtils.byteToUnsignedByte(bytes.get(0));
        return words.computeIfAbsent(value, UnsignedWord::new);
    }

    /**
     * Parse an UnsignedWord from a value
     * @param value
     * @return
     * @throws IllegalArgumentException Provided value is not between 0 and 0xFFFFFFFF, inclusively.
     */
    public static UnsignedWord valueOf(long value){
        HexUtils.assertRange(value, 0, 0xFFFFFFFFL);
        return words.computeIfAbsent(value, UnsignedWord::new);
    }

    /**
     * Upcast an UnsignedByte to an UnsignedWord
     * @param uByte
     * @return
     * @throws NullPointerException uByte is null.
     */
    public static UnsignedWord valueOf(UnsignedByte uByte){
        Objects.requireNonNull(uByte);
        return words.computeIfAbsent((long) uByte.value, UnsignedWord::new);
    }

    /**
     * Upcast an UnsignedShort to an UnsignedWord
     * @param uShort
     * @return
     * @throws NullPointerException uShort is null.
     */
    public static UnsignedWord valueOf(UnsignedShort uShort){
        Objects.requireNonNull(uShort);
        return words.computeIfAbsent((long) uShort.value, UnsignedWord::new);
    }

    @Override
    public long getValue(){
        return value;
    }

    public ByteBuffer toBytes(){
        return HexUtils.toByteBuffer(value, value >>> 8, value >>> 16, value >>> 24);
    }

    @Override
    public String toString() {
        return "UnsignedWord{" +
                "value=" + value +
                '}';
    }

    @Override
    public int compareTo(UnsignedWord o) {
        if(o == null){
            throw new NullPointerException("Null provided");
        }
        return (int)Math.signum(this.value - o.value);
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
        } else if(obj instanceof UnsignedWord) {
            return this.value == ((UnsignedWord) obj).value;
        }
        return false;
    }

    /**
     * Checks for equality amongst other Unsigned types.
     * This allows equality checks between UnsignedWord and either UnsignedByte
     * and UnsignedShort. Essentially,
     * <code>
     *  UnsignedWord.valueOf(0).equalsByValue(UnsignedByte.valueOf(0)) == true;
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
        } else if(obj instanceof UnsignedShort){
            return this.value ==((UnsignedShort) obj).value;
        } else {
            throw new ClassCastException("Obj is type " + obj.getClass().getName() + " and can't be converted to an Unsigned type.");
        }
    }
}
