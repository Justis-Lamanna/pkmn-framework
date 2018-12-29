package com.github.lucbui.pipeline;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates a linear read-write pipeline for populating objects.
 * Objects simply travel in a straight line, through either the readPipe or the writePipe. No branching
 * or early pipe termination can be used with this; thus, it is the simplest type of pipeline.
 */
public class LinearPipeline<T> implements Pipeline<T> {
    private List<ReadPipe<? super T>> readPipes;
    private List<WritePipe<? super T>> writePipes;
    private PkmnFramework pkmnFramework;

    private LinearPipeline(List<ReadPipe<? super T>> readPipes, List<WritePipe<? super T>> writePipes, PkmnFramework pkmnFramework){
        this.readPipes = readPipes;
        this.writePipes = writePipes;
        this.pkmnFramework = pkmnFramework;
    }

    @Override
    public void write(HexFieldIterator iterator, T obj) {
        for(WritePipe<? super T> writePipe : writePipes){
            writePipe.write(iterator, obj, pkmnFramework);
        }
    }

    @Override
    public void modify(HexFieldIterator iterator, T obj){
        for(ReadPipe<? super T> readPipe : readPipes){
            readPipe.read(obj, iterator, pkmnFramework);
        }
    }

    /**
     * Build a LinearPipeline using a Builder
     * @return A builder to continue chaining objects to.
     */
    public static <T> Builder<T> create(){
        return new Builder<>();
    }

    public static class Builder<B> {
        private List<ReadPipe<? super B>> readPipes;
        private List<WritePipe<? super B>> writePipes;
        private PkmnFramework pkmnFramework;

        private Builder(){
            this.readPipes = new ArrayList<>();
            this.writePipes = new ArrayList<>();
        }

        public ReaderBuilder<B> read(ReadPipe<? super B> readPipe){
            Objects.requireNonNull(readPipe);
            return new ReaderBuilder<>(this).then(readPipe);
        }

        public WriterBuilder<B> write(WritePipe<? super B> writePipe){
            Objects.requireNonNull(writePipe);
            return new WriterBuilder<>(this).then(writePipe);
        }

        public LinearPipeline<B> build(){
            return new LinearPipeline<>(readPipes, writePipes, pkmnFramework);
        }

        public Builder<B> framework(PkmnFramework pkmnFramework){
            this.pkmnFramework = pkmnFramework;
            return this;
        }
    }

    public static class ReaderBuilder<B> {
        private Builder<B> baseBuilder;
        private List<ReadPipe<? super B>> readPipes;

        private ReaderBuilder(Builder<B> baseBuilder){
            this.baseBuilder = baseBuilder;
            this.readPipes = new ArrayList<>();
        }

        public ReaderBuilder<B> then(ReadPipe<? super B> readPipe){
            Objects.requireNonNull(readPipe);
            this.readPipes.add(readPipe);
            return this;
        }

        public Builder<B> end(){
            baseBuilder.readPipes = this.readPipes;
            return baseBuilder;
        }
    }

    public static class WriterBuilder<B> {
        private Builder<B> baseBuilder;
        private List<WritePipe<? super B>> writePipes;

        private WriterBuilder(Builder<B> baseBuilder){
            this.baseBuilder = baseBuilder;
            this.writePipes = new ArrayList<>();
        }

        public WriterBuilder<B> then(WritePipe<? super B> writePipe){
            Objects.requireNonNull(writePipe);
            this.writePipes.add(writePipe);
            return this;
        }

        public Builder<B> end(){
            baseBuilder.writePipes = this.writePipes;
            return baseBuilder;
        }
    }
}
