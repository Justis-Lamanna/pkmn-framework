package com.github.lucbui.framework;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.file.FileHexField;
import com.github.lucbui.file.HexField;
import com.github.lucbui.file.HexFieldIterator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A PKMN Framework, which facilitates easier parsing of hex files.
 *
 * To use, call PkmnFramework.init(), and pass a file path. You can then chain additional properties onto it.
 * Changes only take effect when start() is called at the end of the chain.
 *
 * PkmnFramework will then contain static helper methods that can be used to read and write to hex files, by using recursion.
 */
public class PkmnFramework {

    private static HexField hexField = null;

    /**
     * Start creating the framework.
     * @param path The path of the hex file
     * @return A builder, for chaining.
     */
    public static PkmnFramework.Builder init(String path){
        Objects.requireNonNull(path, "Path must be specified.");
        Builder b = new Builder();
        b.path = new File(path);
        return b;
    }

    /**
     * Start creating the framework.
     * @param path The path of the hex file
     * @return A builder, for chaining.
     */
    public static PkmnFramework.Builder init(File path){
        Objects.requireNonNull(path, "Path must be specified.");
        Builder b = new Builder();
        b.path = path;
        return b;
    }

    /**
     * Create a framework from a generic HexField object.
     * @param hexField The hexfield to use.
     * @return A builder, to chain more configuration details.
     */
    public static PkmnFramework.Builder init(HexField hexField){
        Objects.requireNonNull(hexField, "Hex Field must be specified.");
        Builder b = new Builder();
        b.hexField = hexField;
        return b;
    }

    private static void verifyFieldsPresent(){
        if(hexField == null){
            throw new IllegalStateException("PkmnFramework has not been initialized! Use .init() and .start() to initialize.");
        }
    }

    /**
     * Read an object from a pointer
     * @param pointer The pointer to read.
     * @param reader The reader to use.
     * @param <T> The object to extract
     * @return The extracted object.
     */
    public static <T> T read(long pointer, HexReader<T> reader){
        verifyFieldsPresent();
        return reader.translate(hexField.iterator(pointer));
    }

    /**
     * Read an object reflectively from a pointer.
     * @param pointer The pointer to read.
     * @param clazz The reader to use.
     * @param <T> The object to extract
     * @return The extracted object
     */
    public static <T> T read(long pointer, Class<T> clazz){
        verifyFieldsPresent();
        return clazz.cast(ReflectionHexReader.getHexReaderFor(clazz).translate(hexField.iterator(pointer)));
    }

    /**
     * Create an iterator to maneuver the hex field.
     * @param position The position to start the iterator at.
     * @return An iterator.
     */
    public static HexFieldIterator getIterator(long position){
        verifyFieldsPresent();
        return hexField.iterator(position);
    }

    public static class Builder {
        private File path;
        private HexField hexField;

        Map<Class<?>, HexReader<?>> readers;

        private Builder(){
            this.readers = new HashMap<>();
        }

        /**
         * Adds a reader to the class hex parser.
         * If ReflectionHexReader encounters a type listed in this reader, it will call the associated HexReader
         * to parse it, rather than use reflection to do so.
         * @param clazz The class to associate with.
         * @param reader The reader to use.
         * @param <T> The type created by the reader.
         * @return This Builder for additional chaining.
         */
        public <T> Builder addReader(Class<T> clazz, HexReader<T> reader){
            Objects.requireNonNull(clazz);
            Objects.requireNonNull(reader);
            readers.put(clazz, reader);
            return this;
        }

        public void start() throws IOException {
            if(hexField == null) {
                PkmnFramework.hexField = new FileHexField(path);
            } else {
                PkmnFramework.hexField = hexField;
            }
            ReflectionHexReader.READERS.putAll(this.readers);
        }
    }
}
