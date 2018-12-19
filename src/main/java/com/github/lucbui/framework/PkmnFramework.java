package com.github.lucbui.framework;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.config.Configuration;
import com.github.lucbui.config.MutableConfig;
import com.github.lucbui.file.FileHexField;
import com.github.lucbui.file.HexField;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A PKMN Framework, which facilitates easier parsing of hex files.
 *
 * To use, call PkmnFramework.init(), and pass a file path. You can then chain additional properties onto it.
 * Changes only take effect when start() is called at the end of the chain.
 *
 * PkmnFramework will then contain static helper methods that can be used to read and write to hex files, by using recursion.
 */
public class PkmnFramework {

    private HexField hexField = null;
    private Configuration configuration = null;

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

    private void verifyFieldsPresent(){
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
    public <T> T read(Pointer pointer, HexReader<T> reader){
        verifyFieldsPresent();
        return reader.read(hexField.iterator(pointer));
    }

    /**
     * Write an object to a pointer.
     * @param pointer The pointer to write to.
     * @param writer The writer to use.
     * @param object The object to write.
     * @param <T> The object to write.
     */
    public <T> void write(Pointer pointer, HexWriter<T> writer, T object){
        verifyFieldsPresent();
        writer.write(object, hexField.iterator(pointer));
    }

    /**
     * Read an object reflectively from a pointer.
     * @param pointer The pointer to read.
     * @param clazz The reader to use.
     * @param <T> The object to extract
     * @return The extracted object
     */
    public <T> T read(Pointer pointer, Class<T> clazz){
        verifyFieldsPresent();
        return clazz.cast(ReflectionHexReaderWriter.getHexReaderFor(clazz, this).read(hexField.iterator(pointer)));
    }

    /**
     * Write an object reflectively from a pointer.
     *
     * @param pointer The pointer to read.
     * @param object The object to write.
     * @param repointStrategy The strategy to use when repointing.
     * @param <T> The object to write
     */
    public <T> void write(Pointer pointer, T object, RepointStrategy repointStrategy) {
        verifyFieldsPresent();
        if(repointStrategy == null){
            repointStrategy = RepointUtils.disableRepointStrategy();
        }
        ReflectionHexReaderWriter.getHexWriterFor(object.getClass(), repointStrategy, this).writeObject(object, hexField.iterator(pointer));
    }

    /**
     * Write an object reflectively from a pointer.
     * By default, if a PointerObject is encountered, an IllegalStateException will be thrown. To avoid this, supply
     * a RepointStrategy.
     * @param pointer The pointer to read.
     * @param object The object to write.
     * @param <T> The object to write
     */
    public <T> void write(Pointer pointer, T object) {
        write(pointer, object, RepointUtils.disableRepointStrategy());
    }

    /**
     * Create an iterator to maneuver the hex field.
     * @param position The position to start the iterator at.
     * @return An iterator.
     */
    public HexFieldIterator getIterator(Pointer position){
        verifyFieldsPresent();
        return hexField.iterator(position);
    }

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @return The value corresponding to the provided key.
     */
    public Optional<String> getFromConfig(String key){
        return configuration == null ? Optional.empty() : configuration.get(key);
    }

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @param converter A function that converts the value into the object T
     * @return The value corresponding to the provided key.
     */
    public <T> Optional<T> getFromConfig(String key, Function<String, T> converter){
        Objects.requireNonNull(converter);
        return configuration == null ? Optional.empty() : configuration.get(key, converter);
    }

    public static class Builder {
        private File path;
        private HexField hexField;
        private Configuration configuration;

        Map<Class<?>, HexReader<?>> readers;
        Map<Class<?>, HexWriter<?>> writers;

        private Builder(){
            this.readers = new HashMap<>();
            this.writers = new HashMap<>();
        }

        public Builder setConfiguration(Configuration configuration){
            if(configuration == null){
                throw new NullPointerException("Null Configuration specified");
            }
            this.configuration = configuration;
            return this;
        }

        /**
         * Adds a reader to the class hex parser.
         * If ReflectionHexReaderWriter encounters a type listed in this reader, it will call the associated HexReader
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

        /**
         * Adds a writer to the class hex parser.
         * If ReflectionHexReaderWriter encounters a type listed in this writer, it will call the associated HexWriter
         * to parse it, rather than use reflection to do so.
         * @param clazz The class to associate with.
         * @param writer The writer to use.
         * @param <T> The type created by the writer.
         * @return This Builder for additional chaining
         */
        public <T> Builder addWriter(Class<T> clazz, HexWriter<T> writer){
            Objects.requireNonNull(clazz);
            Objects.requireNonNull(writer);
            writers.put(clazz, writer);
            return this;
        }

        /**
         * Adds a reader and a writer to the class hex parser.
         * @param clazz The class to associate with.
         * @param reader The reader to use.
         * @param writer The writer to use.
         * @param <T> The type created by the parsers.
         * @return This Builder for additional chaining.
         */
        public <T> Builder addReaderWriter(Class<T> clazz, HexReader<T> reader, HexWriter<T> writer){
            Objects.requireNonNull(clazz);
            Objects.requireNonNull(reader);
            Objects.requireNonNull(writer);
            readers.put(clazz, reader);
            writers.put(clazz, writer);
            return this;
        }

        /**
         * Applies a FrameworkFactory
         * @param frameworkFactory
         * @return
         */
        public Builder frameworkFactory(FrameworkFactory frameworkFactory) {
            Objects.requireNonNull(frameworkFactory);
            frameworkFactory.configure(this);
            return this;
        }

        public PkmnFramework start() throws IOException {
            PkmnFramework framework = new PkmnFramework();
            if(hexField == null) {
                framework.hexField = new FileHexField(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
            } else {
                framework.hexField = hexField;
            }
            ReflectionHexReaderWriter.resetReaders();
            ReflectionHexReaderWriter.addReaders(this.readers);
            ReflectionHexReaderWriter.resetWriters();
            ReflectionHexReaderWriter.addWriters(this.writers);
            framework.configuration = configuration;
            return framework;
        }
    }
}
