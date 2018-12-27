package com.github.lucbui.pipeline;

import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.Evaluator;
import com.github.lucbui.framework.RepointStrategy;

import java.util.*;

/**
 * Encapsulates a linear read-write pipeline for populating objects.
 * Objects simply travel in a straight line, through either the readPipe or the writePipe. No branching
 * or early pipe termination can be used with this; thus, it is the simplest type of pipeline.
 */
public class LinearPipeline implements Pipeline{
    private final Map<Class<?>, Hexer<?>> hexers;
    private CreatePipe createPipe;
    private List<ReadPipe> readPipes;
    private List<WritePipe> writePipes;
    private Evaluator evaluator;

    private LinearPipeline(CreatePipe createPipe, List<ReadPipe> readPipes, List<WritePipe> writePipes, Evaluator evaluator, Map<Class<?>, Hexer<?>> hexers){
        this.createPipe = createPipe;
        this.readPipes = readPipes;
        this.writePipes = writePipes;
        this.evaluator = evaluator;
        this.hexers = hexers;
    }

    public Evaluator getEvaluator(){
        return evaluator;
    }

    public Map<Class<?>, Hexer<?>> getHexers() {
        return hexers;
    }

    @Override
    public <T> T read(HexFieldIterator iterator, Class<T> clazz){
        T obj = createPipe.create(clazz);
        for(ReadPipe readPipe : readPipes){
            readPipe.read(obj, iterator, this);
        }
        return obj;
    }

    @Override
    public void write(HexFieldIterator iterator, Object obj, RepointStrategy repointStrategy) {
        for(WritePipe writePipe : writePipes){
            writePipe.write(iterator, obj, repointStrategy, this);
        }
    }

    /**
     * Build a LinearPipeline using a Builder
     * @param createPipe The CreatePipe to use.
     * @return A builder to continue chaining objects to.
     */
    public static Builder create(CreatePipe createPipe){
        Objects.requireNonNull(createPipe);
        return new Builder(createPipe);
    }

    public static class Builder {
        private CreatePipe createPipe;
        private List<ReadPipe> readPipes;
        private List<WritePipe> writePipes;
        private Evaluator evaluator;
        private Map<Class<?>, Hexer<?>> hexers;

        private Builder(CreatePipe createPipe){
            this.createPipe = createPipe;
            this.readPipes = new ArrayList<>();
            this.writePipes = new ArrayList<>();
        }

        public ReaderBuilder read(ReadPipe readPipe){
            Objects.requireNonNull(readPipe);
            return new ReaderBuilder(this).then(readPipe);
        }

        public WriterBuilder write(WritePipe writePipe){
            Objects.requireNonNull(writePipe);
            return new WriterBuilder(this).then(writePipe);
        }

        public LinearPipeline build(){
            return new LinearPipeline(createPipe, readPipes, writePipes, evaluator, Collections.unmodifiableMap(hexers));
        }

        public Builder evaluator(Evaluator evaluator) {
            this.evaluator = evaluator;
            return this;
        }

        public Builder hexers(Map<Class<?>, Hexer<?>> hexers){
            this.hexers = hexers;
            return this;
        }
    }

    public static class ReaderBuilder {
        private Builder baseBuilder;
        private List<ReadPipe> readPipes;

        private ReaderBuilder(Builder baseBuilder){
            this.baseBuilder = baseBuilder;
            this.readPipes = new ArrayList<>();
        }

        public ReaderBuilder then(ReadPipe readPipe){
            Objects.requireNonNull(readPipe);
            this.readPipes.add(readPipe);
            return this;
        }

        public Builder end(){
            baseBuilder.readPipes = this.readPipes;
            return baseBuilder;
        }
    }

    public static class WriterBuilder {
        private Builder baseBuilder;
        private List<WritePipe> writePipes;

        private WriterBuilder(Builder baseBuilder){
            this.baseBuilder = baseBuilder;
            this.writePipes = new ArrayList<>();
        }

        public WriterBuilder then(WritePipe writePipe){
            Objects.requireNonNull(writePipe);
            this.writePipes.add(writePipe);
            return this;
        }

        public Builder end(){
            baseBuilder.writePipes = this.writePipes;
            return baseBuilder;
        }
    }
}
