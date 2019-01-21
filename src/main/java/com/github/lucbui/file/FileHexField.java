package com.github.lucbui.file;

import com.github.lucbui.bytes.ByteWindow;
import com.github.lucbui.utility.HexUtils;
import com.github.lucbui.utility.Try;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;

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
    private FileHexField(File file, OpenOption... options) throws IOException {
        fileChannel = FileChannel.open(file.toPath(), options);
    }

    /**
     * Create a FileHexField from a Path object.
     * @param path The path to use.
     * @param options Options to use when opening the file.
     * @throws IOException
     */
    private FileHexField(Path path, OpenOption... options) throws IOException {
        fileChannel = FileChannel.open(path, options);
    }

    /**
     * Create a FileHexField from a File object.
     * @param file The file to use.
     * @param options The options to use when opening the file.
     * @return A Try containing the created FileHexField, or an empty Try if an IOException occured.
     */
    public static Try<FileHexField> get(File file, OpenOption... options){
        return Try.running(() -> new FileHexField(file, options), "Error creating FileHexField");
    }

    /**
     * Create a FileHexField from a Path object.
     * @param path The path to use.
     * @param options Options to use when opening the file.
     * @return A Try containing the created FileHexField, or an empty Try if an IOException occured.
     */
    public static Try<FileHexField> get(Path path, OpenOption... options){
        return Try.running(() -> new FileHexField(path, options), "Error creating FileHexField");
    }

    @Override
    public HexFieldIterator iterator(Pointer position) {
        return new Iterator(this, position.getLocation());
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

        @Override
        public Try<Byte> getByte(long distance){
            return Try.running(() -> {
                ByteBuffer bite = ByteBuffer.allocate(1);
                int read = hex.fileChannel.read(bite, currentPosition + distance);
                if (read != 1) {
                    throw new IllegalStateException("Error reading bytes, expected " + 1 + " byte, got " + read);
                }
                return bite.get(0);
            }, "Error retrieving byte");
        }

        @Override
        public Try<ByteWindow> getRelative(long distance, int numberOfBytes) {
            return Try.running(() -> {
                ByteBuffer bite = ByteBuffer.allocate(numberOfBytes);
                int read = hex.fileChannel.read(bite, currentPosition + distance);
                if(read != numberOfBytes){
                    throw new IllegalStateException("Error reading bytes, expected " + numberOfBytes + " byte, got " + read);
                }
                return new ByteWindow(bite);
            }, "Error retrieving byte");
        }

        @Override
        public Try<Integer> writeRelative(long distance, ByteWindow bytes){
            return Try.running(() -> {
                bytes.forEach((pos, bite) -> {
                    try {
                        hex.fileChannel.write(HexUtils.toByteBuffer(bite), currentPosition + distance + pos);
                    } catch (IOException e) {
                        throw new RuntimeException("Error writing to iterator", e);
                    }
                });
                return 1;
            }, "");
        }

        @Override
        public void advanceRelative(long distance) {
            currentPosition += distance;
        }

        @Override
        public void advanceTo(long pointer) {
            currentPosition = pointer;
        }

        @Override
        public long getPosition() {
            return currentPosition;
        }
    }
}
