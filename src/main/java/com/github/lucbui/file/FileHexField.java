package com.github.lucbui.file;

import com.github.lucbui.bytes.HexReader;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * A hex field derived from a file.
 * Rather than a RandomAccessFile, this field utilizes a FileChannel for arbitrary byte references. This allows us
 * to use the Non-Blocking IO features in Java.
 */
public class FileHexField implements HexField {

    private final FileChannel fileChannel;

    /**
     * Create a FileHexField from a File object.
     * @param file The file to use.
     * @param options The options to use when opening the file.
     * @throws IOException
     */
    public FileHexField(File file, OpenOption... options) throws IOException {
        fileChannel = FileChannel.open(file.toPath(), options);
    }

    /**
     * Create a FileHexField from a Path object.
     * @param path The path to use.
     * @param options Options to use when opening the file.
     * @throws IOException
     */
    public FileHexField(Path path, OpenOption... options) throws IOException {
        fileChannel = FileChannel.open(path, options);
    }

    @Override
    public HexFieldIterator iterator(long position) {
        return new Iterator(this, position);
    }

    /**
     * An iterator which allows manipulating and reading a FileHexField.
     * Note: This class is very much not optimized for parallelization. I'm
     * personally not very good at parallelizing properly.
     */
    private static class Iterator implements HexFieldIterator {

        private long currentPosition;
        private FileHexField hex;

        private Iterator(FileHexField hex, long position){
            this.currentPosition = position;
            this.hex = hex;
        }

        @Override
        public HexFieldIterator copy() {
            return new FileHexField.Iterator(this.hex, this.currentPosition);
        }


        /*
        In this particular instance, we swap the default behavior: Instead of using getRelative(distance) in a loop
        to retrieve bytes for getRelative(distance, numberOfBytes), I use getRelative(distance, numberOfBytes) and retrieve
        the only one for getRelative(distance).
         */

        @Override
        public byte getRelative(long distance) {
            return getRelative(distance, 1).array()[0];
        }

        @Override
        public ByteBuffer getRelative(long distance, int numberOfBytes) {
            try {
                ByteBuffer bite = ByteBuffer.allocate(numberOfBytes);
                int read = hex.fileChannel.read(bite, currentPosition + distance);
                if(read != numberOfBytes){
                    throw new IllegalStateException("Error reading bytes, expected " + numberOfBytes + " byte, got " + read);
                }
                bite.flip();
                return bite;
            } catch (IOException e) {
                throw new IllegalStateException("Error retrieving from iterator", e);
            }
        }

        @Override
        public ByteBuffer get(int numberOfBytes) {
            return getRelative(0, numberOfBytes);
        }

        @Override
        public void advanceRelative(long distance) {
            currentPosition += distance;
        }

        @Override
        public void advanceTo(long pointer) {
            currentPosition = pointer;
        }
    }
}
