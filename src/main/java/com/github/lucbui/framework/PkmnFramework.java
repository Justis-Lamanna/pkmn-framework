package com.github.lucbui.framework;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.config.Configuration;
import com.github.lucbui.config.MutableConfig;
import com.github.lucbui.file.FileHexField;
import com.github.lucbui.file.HexField;
import com.github.lucbui.file.HexFieldIterator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    private static HexField hexField = null;
    private static Configuration configuration = null;

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
        return reader.read(hexField.iterator(pointer));
    }

    /**
     * Write an object to a pointer.
     * @param pointer The pointer to write to.
     * @param writer The writer to use.
     * @param object The object to write.
     * @param <T> The object to write.
     */
    public static <T> void write(long pointer, HexWriter<T> writer, T object){
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
    public static <T> T read(long pointer, Class<T> clazz){
        verifyFieldsPresent();
        return clazz.cast(ReflectionHexReaderWriter.getHexReaderFor(clazz).read(hexField.iterator(pointer)));
    }

    /**
     * Write an object reflectively from a pointer.
     *
     * @param pointer The pointer to read.
     * @param object The object to write.
     * @param repointStrategy The strategy to use when repointing.
     * @param <T> The object to write
     */
    public static <T> void write(long pointer, T object, RepointStrategy repointStrategy) {
        verifyFieldsPresent();
        if(repointStrategy == null){
            repointStrategy = RepointUtils.disableRepointStrategy();
        }
        ReflectionHexReaderWriter.getHexWriterFor(object.getClass(), repointStrategy).writeObject(object, hexField.iterator(pointer));
    }

    /**
     * Write an object reflectively from a pointer.
     * By default, if a PointerObject is encountered, an IllegalStateException will be thrown. To avoid this, supply
     * a RepointStrategy.
     * @param pointer The pointer to read.
     * @param object The object to write.
     * @param <T> The object to write
     */
    public static <T> void write(long pointer, T object) {
        write(pointer, object, RepointUtils.disableRepointStrategy());
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

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @param def The default value.
     * @return The value corresponding to the provided key.
     */
    public static String getFromConfig(String key, String def){
        return configuration == null ? def : configuration.get(key, def);
    }

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @param def The default value.
     * @return The value corresponding to the provided key.
     */
    public static byte getFromConfig(String key, byte def){
        return configuration == null ? def : configuration.get(key, def);
    }

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @param def The default value.
     * @return The value corresponding to the provided key.
     */
    public static short getFromConfig(String key, short def){
        return configuration == null ? def : configuration.get(key, def);
    }

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @param def The default value.
     * @return The value corresponding to the provided key.
     */
    public static int getFromConfig(String key, int def){
        return configuration == null ? def : configuration.get(key, def);
    }

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @param def The default value.
     * @return The value corresponding to the provided key.
     */
    public static long getFromConfig(String key, long def){
        return configuration == null ? def : configuration.get(key, def);
    }

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @param def The default value.
     * @return The value corresponding to the provided key.
     */
    public static float getFromConfig(String key, float def){
        return configuration == null ? def : configuration.get(key, def);
    }

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @param def The default value.
     * @return The value corresponding to the provided key.
     */
    public static double getFromConfig(String key, double def){
        return configuration == null ? def : configuration.get(key, def);
    }

    /**
     * Get a value from the configuration provided.
     * If no configuration was provided, the default is provided.
     * @param key The key to retrieve.
     * @param converter A function that converts the value into the object T
     * @param def The default value.
     * @return The value corresponding to the provided key.
     */
    public static <T> T getFromConfig(String key, Function<String, T> converter, T def){
        Objects.requireNonNull(converter);
        return (configuration == null || !configuration.has(key)) ? def : converter.apply(configuration.get(key));
    }

    /**
     * Set a value in the configuration.
     * Throws an IllegalArgumentException is no saveable configuration is set.
     * @param key The key
     * @param value The value
     */
    public static void setInConfig(String key, String value){
        verifySaving();
        ((MutableConfig) configuration).set(key, value);
    }

    /**
     * Set a value in the configuration.
     * Throws an IllegalArgumentException is no saveable configuration is set.
     * @param key The key
     * @param value The value
     */
    public static void setInConfig(String key, byte value){
        verifySaving();
        ((MutableConfig) configuration).set(key, value);
    }

    /**
     * Set a value in the configuration.
     * Throws an IllegalArgumentException is no saveable configuration is set.
     * @param key The key
     * @param value The value
     */
    public static void setInConfig(String key, short value){
        verifySaving();
        ((MutableConfig) configuration).set(key, value);
    }

    /**
     * Set a value in the configuration.
     * Throws an IllegalArgumentException is no saveable configuration is set.
     * @param key The key
     * @param value The value
     */
    public static void setInConfig(String key, int value){
        verifySaving();
        ((MutableConfig) configuration).set(key, value);
    }

    /**
     * Set a value in the configuration.
     * Throws an IllegalArgumentException is no saveable configuration is set.
     * @param key The key
     * @param value The value
     */
    public static void setInConfig(String key, long value){
        verifySaving();
        ((MutableConfig) configuration).set(key, value);
    }

    /**
     * Set a value in the configuration.
     * Throws an IllegalArgumentException is no saveable configuration is set.
     * @param key The key
     * @param value The value
     */
    public static void setInConfig(String key, float value){
        verifySaving();
        ((MutableConfig) configuration).set(key, value);
    }

    /**
     * Set a value in the configuration.
     * Throws an IllegalArgumentException is no saveable configuration is set.
     * @param key The key
     * @param value The value
     */
    public static void setInConfig(String key, double value){
        verifySaving();
        ((MutableConfig) configuration).set(key, value);
    }

    /**
     * Set a value in the configuration.
     * Throws an IllegalArgumentException is no saveable configuration is set.
     * @param key The key
     * @param value The value
     */
    public static <T> void setInConfig(String key, T value, Function<T, String> converter){
        verifySaving();
        ((MutableConfig) configuration).set(key, value, converter);
    }

    /**
     * Save the configuration.
     * Throws an IllegalArgumentException is no saveable configuration is set.
     * @param os The place to save it.
     */
    public static void saveConfig(OutputStream os){
        verifySaving();
        ((MutableConfig) configuration).save(os);
    }

    private static void verifySaving(){
        if(!(configuration instanceof MutableConfig)){
            throw new IllegalArgumentException("No saveable configuration specified.");
        }
    }

    public static class Builder {
        private File path;
        private HexField hexField;
        private Configuration configuration;

        Map<Class<?>, HexReader<?>> readers;
        Map<Class<?>, HexWriter<?>> writers;
        Map<Class<?>, ReflectionAnnotationFunction> annotations;

        private Builder(){
            this.readers = new HashMap<>();
            this.writers = new HashMap<>();
            this.annotations = new HashMap<>();
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

        public Builder addReflectionAnnotationFunction(Class<?> annotationClazz, ReflectionAnnotationFunction raf){
            Objects.requireNonNull(annotationClazz);
            Objects.requireNonNull(raf);
            annotations.put(annotationClazz, raf);
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

        public void start() throws IOException {
            if(hexField == null) {
                PkmnFramework.hexField = new FileHexField(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
            } else {
                PkmnFramework.hexField = hexField;
            }
            ReflectionHexReaderWriter.resetReaders();
            ReflectionHexReaderWriter.addReaders(this.readers);
            ReflectionHexReaderWriter.resetWriters();
            ReflectionHexReaderWriter.addWriters(this.writers);
            ReflectionHexReaderWriter.resetAnnotations();
            ReflectionHexReaderWriter.addAnnotations(this.annotations);
            PkmnFramework.configuration = configuration;
        }
    }
}
