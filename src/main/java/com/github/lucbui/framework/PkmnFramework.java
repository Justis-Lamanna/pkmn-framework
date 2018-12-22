package com.github.lucbui.framework;

import com.github.lucbui.annotations.FromConfig;
import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.config.Configuration;
import com.github.lucbui.config.MutableConfig;
import com.github.lucbui.file.FileHexField;
import com.github.lucbui.file.HexField;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.StandardOpenOption;
import java.util.*;
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
    private Evaluator evaluator = null;

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

    /**
     * Read an object from a pointer
     * @param pointer The pointer to read.
     * @param reader The reader to use.
     * @param <T> The object to extract
     * @return The extracted object.
     */
    public <T> T read(Pointer pointer, HexReader<T> reader){
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
        return clazz.cast(ReflectionHexReaderWriter.getHexerFor(clazz, evaluator).read(hexField.iterator(pointer)));
    }

    /**
     * Write an object reflectively from a pointer.
     *
     * @param pointer The pointer to read.
     * @param object The object to write.
     * @param <T> The object to write
     */
    public <T> void write(Pointer pointer, T object) {
        ReflectionHexReaderWriter.getHexerFor(object.getClass(), evaluator).writeObject(object, hexField.iterator(pointer));
    }

    /**
     * Read an object reflectively from a pointer.
     * @param pointer The pointer to read.
     * @param evaluator The evaluator to use.
     * @param clazz The reader to use.
     * @param <T> The object to extract
     * @return The extracted object
     */
    public <T> T read(Pointer pointer, Evaluator evaluator, Class<T> clazz){
        return clazz.cast(ReflectionHexReaderWriter.getHexerFor(clazz, evaluator).read(hexField.iterator(pointer)));
    }

    /**
     * Write an object reflectively from a pointer.
     *
     * @param pointer The pointer to read.
     * @param evaluator The evaluator to use.
     * @param object The object to write.
     * @param <T> The object to write
     */
    public <T> void write(Pointer pointer, Evaluator evaluator, T object) {
        ReflectionHexReaderWriter.getHexerFor(object.getClass(), evaluator).writeObject(object, hexField.iterator(pointer));
    }

    /**
     * Create an iterator to maneuver the hex field.
     * @param position The position to start the iterator at.
     * @return An iterator.
     */
    public HexFieldIterator getIterator(Pointer position){
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
        private Evaluator evaluator;

        Map<Class<?>, Hexer<?>> hexers;

        private Builder(){
            this.hexers = new HashMap<>();
        }

        public Builder setConfiguration(Configuration configuration){
            if(configuration == null){
                throw new NullPointerException("Null Configuration specified");
            }
            this.configuration = configuration;
            return this;
        }

        public <T> Builder addHexer(Class<T> clazz, Hexer<T> hexer){
            Objects.requireNonNull(clazz);
            Objects.requireNonNull(hexer);
            hexers.put(clazz, hexer);
            return this;
        }

        /**
         * Set the default Evaluator to use when parsing out class objects.
         * If an Evaluator is not used, but a Configuration is provided, a ConfigurationEvaluator is used.
         * @param evaluator The evaluator to use.
         * @return
         */
        public Builder setEvaluator(Evaluator evaluator){
            Objects.requireNonNull(evaluator);
            this.evaluator = evaluator;
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
            ReflectionHexReaderWriter.resetHexers();
            ReflectionHexReaderWriter.addHexer(this.hexers);
            framework.configuration = configuration;
            if(evaluator == null) {
                if(configuration == null){
                    framework.evaluator = new IdentityEvaluator();
                } else {
                    framework.evaluator = new ConfigurationEvaluator(configuration);
                }
            }
            return framework;
        }
    }
}
