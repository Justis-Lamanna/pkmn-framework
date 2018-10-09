package com.github.lucbui.file;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;

import java.nio.ByteBuffer;

/**
 * A custom iterator, modified to better handle fields of data.
 *
 * The traditional iterator follows a "get-and-advance" approach. This doesn't lend itself
 * particularly well to parsing bytes. This approach separates the "get" and "advance" steps, allowing the
 * user to look forwards or backwards relative from their current location before advancing onwards.
 */
public interface HexFieldIterator {

    /**
     * Copy this HexFieldIterator, state and all
     * @return
     */
    HexFieldIterator copy();

    /**
     * Create a copy of this HexFieldIterator, moving it to the specified pointer.
     * @param pointer The position to start at.
     * @return
     */
    default HexFieldIterator copy(long pointer){
        HexFieldIterator copy = this.copy();
        copy.advanceTo(pointer);
        return copy;
    }

    default HexFieldIterator copyRelative(long relative){
        HexFieldIterator copy = this.copy();
        copy.advanceRelative(relative);
        return copy;
    }

    /**
     * Get a number of bytes relativeIndex away from the current position.
     * This advances the specified number of bytes forward, and then reads the specified number of bytes all at once.
     *
     * A positive distance should indicate moving forward past the iterator. A negative distance
     * should indicate moving backwards, away from the iterator. A distance of zero should return the current
     * byte.
     *
     * @param distance The number of bytes away where reading should begin.
     * @param numberOfBytes The number of bytes to read.
     * @return The read bytes.
     */
    ByteBuffer getRelative(long distance, int numberOfBytes);

    /**
     * Write some bytes relativeIndex away from the current position
     * This advances the specified number of bytes forward, and writes the bytes in the bytebuffer all at once.
     *
     * A positive distance should indicate moving forward past the iterator. A negative distance
     * should indicate moving backwards, away from the iterator.
     * @param distance The number of bytes away where writing should begin.
     * @param bytes The bytes to write.
     */
    void writeRelative(long distance, ByteBuffer bytes);

    /**
     * Get a number of bytes, starting at the current position.
     * @param numberOfBytes The number of bytes to read.
     * @return The read bytes.
     */
    default ByteBuffer get(int numberOfBytes){
        return getRelative(0, numberOfBytes);
    }

    /**
     * Writes bytes, starting at the current position.
     * @param bytes The bytes to write.
     */
    default void write(ByteBuffer bytes){writeRelative(0, bytes);}


    /**
     * Read an object from an absolute position
     * @param pointer Pointer to read from
     * @param reader The reader to use.
     * @param <T> The object to retrieve.
     * @return
     */
    default <T> T getAbsolute(long pointer, HexReader<T> reader){
        HexFieldIterator copy = this.copy();
        copy.advanceTo(pointer);
        return reader.read(copy);
    }

    /**
     * Write an object to an absolute position
     * @param pointer The pointer to write to
     * @param writer The writer to use.
     * @param object The object to write.
     * @param <T> The object to write.
     */
    default <T> void writeAbsolute(T object, long pointer, HexWriter<T> writer){
        HexFieldIterator copy = this.copy();
        copy.advanceTo(pointer);
        writer.write(object, copy);
    }

    /**
     * Advances the iterator forward some amount.
     * A positive distance should indicate moving forward past the iterator. A negative distance
     * should indicate moving backwards, away from the iterator. A distance of zero should return the current
     * byte.
     * @param distance The distance to move, relative to the current position.
     */
    void advanceRelative(long distance);

    /**
     * Advances the iterator to a hard-set pointer.
     * @param pointer The pointer to move to.
     */
    void advanceTo(long pointer);
}
