package com.github.lucbui.file;

import com.github.lucbui.bytes.ByteWindow;
import com.github.lucbui.bytes.HexUtils;

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

        @Override
        public byte getByte(long distance){
            try {
                ByteBuffer bite = ByteBuffer.allocate(1);
                int read = hex.fileChannel.read(bite, currentPosition + distance);
                if (read != 1) {
                    throw new IllegalStateException("Error reading bytes, expected " + 1 + " byte, got " + read);
                }
                return bite.get(0);
            } catch (IOException e) {
                throw new IllegalStateException("Error retrieving from iterator", e);
            }
        }

        @Override
        public ByteWindow getRelative(long distance, int numberOfBytes) {
            try {
                ByteBuffer bite = ByteBuffer.allocate(numberOfBytes);
                int read = hex.fileChannel.read(bite, currentPosition + distance);
                if(read != numberOfBytes){
                    throw new IllegalStateException("Error reading bytes, expected " + numberOfBytes + " byte, got " + read);
                }
                return new ByteWindow(bite);
            } catch (IOException e) {
                throw new IllegalStateException("Error retrieving from iterator", e);
            }
        }

        @Override
        public void writeRelative(long distance, ByteWindow bytes){
            bytes.forEach((pos, bite) -> {
                try {
                    hex.fileChannel.write(HexUtils.toByteBuffer(bite), distance + pos);
                } catch (IOException e) {
                    throw new RuntimeException("Error writing to iterator", e);
                }
            });
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
