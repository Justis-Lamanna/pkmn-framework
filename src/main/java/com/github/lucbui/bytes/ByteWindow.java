package com.github.lucbui.bytes;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

import com.github.lucbui.file.HexField;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.gba.exception.IllegalSizeException;
import org.apache.commons.lang3.NotImplementedException;

/**
 * A class that represents a subset of memory.
 * A ByteWindow can serve as "working memory" when needing to store something through a HexReader or Writer.
 * They are, theoretically, infinite in size. The ByteWindow may have "holes" in it, which, when written to ROM, will
 * be unchanged.
 *
 * During reading or insertion, negative values are permitted. This allows you to write backward, relative to the insertion
 * point.
 *
 * I wrote this because I hated ByteBuffers.
 */
public class ByteWindow implements HexField {

    public static byte DEFAULT_VALUE = 0;
    private byte defaultValue;
    private Map<Long, Byte> bytes;

    /**
     * Create a blank ByteWindow.
     */
    public ByteWindow() {
        this(DEFAULT_VALUE);
    }

    /**
     * Create a blank ByteWindow from a ByteBuffer.
     * @param bb The ByteBuffer to use.
     */
    public ByteWindow(ByteBuffer bb) {
        this(DEFAULT_VALUE);
        Objects.requireNonNull(bb);
        this.set(0, bb);
    }

    /**
     * Create a blank ByteWindow, using the specified default value.
     * @param defaultValue The default value to use when retrieving a "hole".
     */
    public ByteWindow(byte defaultValue) {
        this.bytes = new TreeMap<>();
        this.defaultValue = defaultValue;
    }

    /**
     * Get the byte at the specified position.
     * @param position The position to use.
     * @return
     */
    public byte get(long position) {
        return this.bytes.getOrDefault(position, this.defaultValue);
    }

    /**
     * Get a number of bytes from the specified position.
     * @param position The position to use.
     * @param number The number of bytes to get.
     * @return
     */
    public byte[] get(long position, int number) {
        if(number < 0){
            throw new IllegalSizeException("number of bytes requested must be non-negative");
        }
        byte[] bites = new byte[number];

        for(int idx = 0; idx < number; ++idx) {
            bites[idx] = this.get(position + (long)idx);
        }

        return bites;
    }

    /**
     * Set a byte at the specified position.
     * @param position The position to use.
     * @param val The byte to set to.
     */
    public void set(long position, byte val) {
        this.bytes.put(position, val);
    }

    /**
     * Set a number of bytes at the specified position.
     * @param position The position to use.
     * @param val The bytes to set to.
     */
    public void set(long position, byte[] val) {
        Objects.requireNonNull(val);
        for(int idx = 0; idx < val.length; ++idx) {
            this.set(position + (long)idx, val[idx]);
        }

    }

    /**
     * Insert a ByteBuffer at the specified position
     * @param position The position to use.
     * @param biteBuffer The ByteBuffer to use.
     * @throws NotImplementedException ByteBuffer provided is not backed by a byte array.
     */
    public void set(long position, ByteBuffer biteBuffer) {
        Objects.requireNonNull(biteBuffer);
        if (biteBuffer.hasArray()) {
            this.set(position, biteBuffer.array());
        } else {
            throw new NotImplementedException("Can't handle byteBuffers not backed by arrays");
        }
    }

    /**
     * Insert another ByteWindow into this one.
     * @param position The position to insert it.
     * @param biteWindow The ByteBuffer to insert.
     */
    public void set(long position, ByteWindow biteWindow){
        Objects.requireNonNull(biteWindow);
        if(position == 0 && biteWindow == this){
            //Nice try
            return;
        }
        Map<Long, Byte> combination = new HashMap<>(this.bytes);
        biteWindow.forEach((pos, bite) -> {
            combination.put(pos + position, bite);
        });
        this.bytes = combination;
    }

    /**
     * Get the bounds on this ByteWindow.
     * This is the largest position in the window minus the smallest. Holes are ignored.
     * @return
     */
    public long getRange() {
        if(bytes.isEmpty()){
            return 0;
        }
        LongSummaryStatistics stats = this.bytes.keySet().stream().mapToLong(k -> k).summaryStatistics();
        return (stats.getMax() + 1) - stats.getMin();
    }

    /**
     * Get the number of set bytes in this ByteWindow.
     * @return
     */
    public int getNumberOfBytes() {
        return this.bytes.size();
    }

    /**
     * Check if this window has no holes.
     * Basically, if the range is equal to the number of bytes specified.
     * @return
     */
    public boolean hasNoHoles() {
        return this.getRange() == (long)this.getNumberOfBytes();
    }

    /**
     * Get a portion of this window into another window.
     * @param fromPtr The starting pointer to use, inclusive.
     * @param toPtr The ending pointer to use, exclusive.
     * @return A subwindow.
     */
    public ByteWindow subWindow(long fromPtr, long toPtr) {
        if(fromPtr > toPtr){
            throw new IllegalArgumentException("fromPtr must be <= toPtr");
        }
        ByteWindow window = new ByteWindow();
        window.bytes = new TreeMap<>();
        this.bytes.keySet().stream().filter(k -> k >= fromPtr && k < toPtr)
                .forEach(k -> window.bytes.put(k, this.bytes.get(k)));
        return window;
    }

    /**
     * Iterate over each byte in the window.
     * @param consumer The consumer to use.
     */
    public void forEach(BiConsumer<? super Long, ? super Byte> consumer) {
        Objects.requireNonNull(consumer);
        this.bytes.forEach(consumer);
    }

    /**
     * Creates a copy of this ByteWindow.
     * @return
     */
    public ByteWindow copy() {
        ByteWindow newByteWindow = new ByteWindow(defaultValue);
        newByteWindow.bytes = new TreeMap<>(this.bytes);
        return newByteWindow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteWindow that = (ByteWindow) o;
        return Objects.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bytes);
    }

    @Override
    public HexFieldIterator iterator(long position) {
        return new ByteWindowHexFieldIterator(this, position);
    }

    private class ByteWindowHexFieldIterator implements HexFieldIterator {

        private ByteWindow byteWindow;
        private long current;

        public ByteWindowHexFieldIterator(ByteWindow byteWindow, long position) {
            this.byteWindow = byteWindow;
            this.current = position;
        }

        @Override
        public HexFieldIterator copy() {
            return new ByteWindowHexFieldIterator(byteWindow.copy(), current);
        }

        @Override
        public ByteWindow getRelative(long distance, int numberOfBytes) {
            return byteWindow.subWindow(current + distance, (current + distance) + numberOfBytes);
        }

        @Override
        public void writeRelative(long distance, ByteWindow bytes) {
            byteWindow.set(current + distance, bytes);
        }

        @Override
        public byte getByte(long distance) {
            return byteWindow.get(current + distance);
        }

        @Override
        public void advanceRelative(long distance) {
            current += distance;
        }

        @Override
        public void advanceTo(long pointer) {
            current = pointer;
        }

        @Override
        public long getPosition() {
            return current;
        }
    }
}
