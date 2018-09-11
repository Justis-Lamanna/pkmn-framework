package com.github.lucbui.file;

import com.github.lucbui.bytes.HexReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    HexFieldIterator copy() throws IOException;

    /**
     * Get the byte distance bytes away from the current position.
     * A positive distance should indicate moving forward past the iterator. A negative distance
     * should indicate moving backwards, away from the iterator. A distance of zero should return the current
     * byte.
     * @param distance The relative number of bytes away to read from.
     * @return The read byte.
     */
    byte getRelative(long distance);

    /**
     * Get a number of bytes relativeIndex away from the current position.
     * This advances the specified number of bytes forward, and then reads the specified number of bytes all at once.
     *
     * A positive distance should indicate moving forward past the iterator. A negative distance
     * should indicate moving backwards, away from the iterator. A distance of zero should return the current
     * byte.
     *
     * By default, this function as a for-loop, calling getRelative starting at {@code distance} (inclusively) and ending at
     * {@code distance + numberOfBytes}, exclusively.
     * @param distance The number of bytes away where reading should begin.
     * @param numberOfBytes The number of bytes to read.
     * @return The read bytes.
     */
    default ByteBuffer getRelative(long distance, int numberOfBytes){
        if(numberOfBytes < 0){
            throw new IllegalArgumentException("Number of bytes < 0 (" + numberOfBytes + ")");
        } else {
            ByteBuffer bb = ByteBuffer.allocate(numberOfBytes);
            Stream.iterate(distance, i -> i + 1)
                    .limit(distance + numberOfBytes)
                    .map(this::getRelative)
                    .forEach(bb::put);
            return bb;
        }
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

    /**
     * Get the current byte.
     * Shortcut for {@code getRelative(0)}
     * @return The current byte.
     */
    default byte get(){
        return getRelative(0);
    }

    /**
     * Get a number of bytes, starting at the current position.
     * @param numberOfBytes The number of bytes to read.
     * @return The read bytes.
     */
    default ByteBuffer get(int numberOfBytes){
        return getRelative(0, numberOfBytes);
    }

    /**
     * Get the next byte.
     * @return The next byte.
     */
    default byte getNext(){
        return getRelative(1);
    }

    /**
     * Get the previous byte.
     * @return The previous byte.
     */
    default byte getPrevious(){
        return getRelative(-1);
    }

    /**
     * Advance forward one byte.
     */
    default void advance(){
        advanceRelative(1);
    }

    /**
     * Advance back one byte.
     */
    default void advanceBack(){
        advanceRelative(-1);
    }

    /**
     * Get a parsed byte structure.
     * @param hexReader The reader which parses the bytes into an object.
     * @param <T> The object to convert the read bytes into.
     * @return The read and parsed object.
     */
    default <T> T get(HexReader<T> hexReader){
        try {
            return hexReader.translate(this.copy());
        } catch (IOException e) {
            throw new IllegalStateException("Error copying iterator", e);
        }
    }

    /**
     * Get a parsed byte structure.
     * @param hexReader The reader which parses the bytes into an object.
     * @param <T> The object to convert the read bytes into.
     * @return The read and parsed object.
     */
    default <T> T get(int distance, HexReader<T> hexReader){
        try {
            HexFieldIterator copy = this.copy();
            copy.advanceRelative(distance);
            return hexReader.translate(copy);
        } catch (IOException e) {
            throw new IllegalStateException("Error copying iterator", e);
        }
    }
}
