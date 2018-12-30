package com.github.lucbui.pipeline;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.pipes.ForEachPipe;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Encapsulates a linear read-write pipeline for populating objects.
 * Objects simply travel in a straight line, through either the readPipe or the writePipe. No branching
 * or early pipe termination can be used with this; thus, it is the simplest type of pipeline.
 */
public class LinearPipeline<T> implements Pipeline<T> {
    private List<ReadPipe<? super T>> readPipes;
    private List<WritePipe<? super T>> writePipes;

    LinearPipeline(List<ReadPipe<? super T>> readPipes, List<WritePipe<? super T>> writePipes){
        this.readPipes = readPipes;
        this.writePipes = writePipes;
    }

    @Override
    public void write(HexFieldIterator iterator, T obj, PkmnFramework pkmnFramework) {
        for(WritePipe<? super T> writePipe : writePipes){
            writePipe.write(iterator, obj, pkmnFramework);
        }
    }

    @Override
    public void modify(HexFieldIterator iterator, T obj, PkmnFramework pkmnFramework){
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

    public static class Builder<B> extends PipelineBuilder<Builder<B>, LinearPipeline<B>, ReadPipe<? super B>, WritePipe<? super B>>{

        @Override
        protected Builder<B> self() {
            return this;
        }

        @Override
        public LinearPipeline<B> build() {
            return new LinearPipeline<>(readers, writers);
        }

        public <O> ForEachPipelineBuilder<O> forEach(Function<B, Stream<O>> forEachFunction){
            return new ForEachPipelineBuilder<>(this, forEachFunction);
        }

        public class ForEachPipelineBuilder<O> extends PipelineBuilder<
                ForEachPipelineBuilder<O>,
                Builder<B>,
                ReadPipe<? super O>,
                WritePipe<? super O>>{

            private final Builder<B> base;
            private Function<B, Stream<O>> forEachFunction;

            private ForEachPipelineBuilder(Builder<B> base, Function<B, Stream<O>> forEachFunction){
                this.base = base;
                this.forEachFunction = forEachFunction;
            }

            @Override
            protected ForEachPipelineBuilder<O> self() {
                return this;
            }

            @Override
            public Builder<B> build() {
                Pipeline<O> pipeline = new LinearPipeline<>(this.readers, this.writers);
                base.readers.add(new ForEachPipe<>(this.forEachFunction, pipeline));
                base.writers.add(new ForEachPipe<>(this.forEachFunction, pipeline));
                return base;
            }
        }
    }
}
