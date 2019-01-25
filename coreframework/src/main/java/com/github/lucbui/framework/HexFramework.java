package com.github.lucbui.framework;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.config.Configuration;
import com.github.lucbui.config.MapConfig;
import com.github.lucbui.evaluator.Evaluator;
import com.github.lucbui.file.FileHexField;
import com.github.lucbui.file.HexField;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.pipeline.Pipeline;
import com.github.lucbui.strategy.CreateStrategy;
import com.github.lucbui.strategy.EmptyConstructorCreateStrategy;
import com.github.lucbui.utility.HexerUtils;
import com.github.lucbui.utility.PipeUtils;
import com.github.lucbui.utility.Try;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;

/**
 * A Hex Framework, which facilitates easier parsing of hex files.
 */
public class HexFramework {

    private CreateStrategy createStrategy = null;
    private HexField hexField = null;
    private Configuration configuration = null;
    private Evaluator evaluator = null;
    private Pipeline<Object> pipeline = null;
    private Map<Class<?>, Hexer<?>> hexers;

    /**
     * Start creating the framework.
     * @param path The path of the hex file
     * @return A builder, for chaining.
     */
    public static HexFramework.Builder init(String path){
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
    public static HexFramework.Builder init(File path){
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
    public static HexFramework.Builder init(HexField hexField){
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
     * Read an object from a pointer
     * @param pointer The pointer to read.
     * @param reader The reader to use.
     * @param <T> The object to extract
     * @return The extracted object.
     */
    public <T> T read(long pointer, HexReader<T> reader){
        return reader.read(hexField.iterator(Pointer.of(pointer)));
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
     * Write an object to a pointer.
     * @param pointer The pointer to write to.
     * @param writer The writer to use.
     * @param object The object to write.
     * @param <T> The object to write.
     */
    public <T> void write(long pointer, HexWriter<T> writer, T object){
        writer.write(object, hexField.iterator(Pointer.of(pointer)));
    }

    /**
     * Read an object reflectively from a pointer.
     * @param pointer The pointer to read.
     * @param clazz The reader to use.
     * @param <T> The object to extract
     * @return The extracted object
     */
    public <T> T read(Pointer pointer, Class<T> clazz){
        T object = createStrategy.create(clazz);
        pipeline.modify(hexField.iterator(pointer), object, this);
        return object;
    }

    /**
     * Read an object reflectively from a pointer.
     * @param pointer The pointer to read.
     * @param clazz The reader to use.
     * @param <T> The object to extract
     * @return The extracted object
     */
    public <T> T read(long pointer, Class<T> clazz){
        T object = createStrategy.create(clazz);
        pipeline.modify(hexField.iterator(Pointer.of(pointer)), object, this);
        return object;
    }

    /**
     * Write an object reflectively from a pointer.
     *
     * If a PointerObject with annotation is encountered, an exception is thrown. Please pass a repoint strategy
     * if you need to do repointing.
     *
     * @param pointer The pointer to read.
     * @param object The object to write.
     * @param <T> The object to write
     */
    public <T> void write(Pointer pointer, T object) {
        pipeline.write(hexField.iterator(pointer), object, this);
    }

    /**
     * Write an object reflectively from a pointer.
     *
     * If a PointerObject with annotation is encountered, an exception is thrown. Please pass a repoint strategy
     * if you need to do repointing.
     *
     * @param pointer The pointer to read.
     * @param object The object to write.
     * @param <T> The object to write
     */
    public <T> void write(long pointer, T object) {
        pipeline.write(hexField.iterator(Pointer.of(pointer)), object, this);
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
     * Get a hexer registered, if present
     * @param clazz The class to search
     * @param <T> The type to read out
     * @return A Hexer, if present
     */
    public <T> Optional<Hexer<T>> getHexerFor(Class<T> clazz){
        return HexerUtils.getHexerFor(hexers, clazz);
    }

    /**
     * Calculate the size of an object
     * @param obj The object to calculate
     * @return The size, or an empty OptionalInt if the size was undetermined.
     */
    public OptionalInt getSize(Object obj){
        return HexerUtils.calculateSizeOfObject(this, obj);
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

    /**
     * Get the evaluator used in this framework.
     * @return
     */
    public Evaluator getEvaluator() {
        return evaluator;
    }

    /**
     * Get the hexers registered to this framework.
     * @return
     */
    public Map<Class<?>, Hexer<?>> getHexers() {
        return hexers;
    }

    /**
     * Get the pipeline used for this framework.
     * @return
     */
    public Pipeline<Object> getPipeline() {
        return pipeline;
    }

    /**
     * Get the createstrategy of this framework.
     * @return
     */
    public CreateStrategy getCreateStrategy() {
        return createStrategy;
    }

    public static class Builder {
        private File path;
        private HexField hexField;
        private Configuration configuration;
        private Evaluator evaluator;
        private Pipeline<Object> pipeline;
        private CreateStrategy createStrategy;

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

        public Builder createFactory(CreateStrategy createStrategy){
            this.createStrategy = createStrategy;
            return this;
        }

        /**
         * Set the default Evaluator to use when parsing out class objects.
         * If an Evaluator is not used, but a Configuration is provided, a ConfigurationEvaluator is used.
         * @param evaluator The evaluator to use.
         * @return This builder
         */
        public Builder setEvaluator(Evaluator evaluator){
            Objects.requireNonNull(evaluator);
            this.evaluator = evaluator;
            return this;
        }

        /**
         * Set the Pipeline to use when reading or writing objects
         * @param pipeline The pipeline to use
         * @return This builder
         */
        public Builder setPipeline(Pipeline<Object> pipeline){
            Objects.requireNonNull(pipeline);
            this.pipeline = pipeline;
            return this;
        }

        /**
         * Applies a FrameworkFactory
         * A framework factory can apply certain presets to this Builder, such as standard hexers or pipelines.
         * @param frameworkFactory The framework factory
         * @return This builder
         */
        public Builder frameworkFactory(FrameworkFactory frameworkFactory) {
            Objects.requireNonNull(frameworkFactory);
            frameworkFactory.configure(this);
            return this;
        }

        /**
         * Build the HexFramework
         * @return
         * @throws IOException
         */
        public Try<HexFramework> build(){
            return Try.running(() -> {
                HexFramework framework = new HexFramework();
                framework.hexers = Collections.unmodifiableMap(hexers);
                if (hexField == null) {
                    framework.hexField = FileHexField.get(path, StandardOpenOption.READ, StandardOpenOption.WRITE).orThrow(RuntimeException::new);
                } else {
                    framework.hexField = hexField;
                }
                if (configuration == null) {
                    configuration = new MapConfig();
                }
                framework.configuration = configuration;
                if (evaluator == null) {
                    evaluator = new ConfigurationEvaluator(configuration);
                }
                framework.evaluator = evaluator;
                if (pipeline == null) {
                    pipeline = PipeUtils.getDefaultPipeline();
                }
                framework.pipeline = pipeline;
                if (createStrategy == null) {
                    createStrategy = new EmptyConstructorCreateStrategy();
                }
                framework.createStrategy = createStrategy;
                return framework;
            }, "Error creating HexFramework");
        }
    }
}
