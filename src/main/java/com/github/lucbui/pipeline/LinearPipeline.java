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

    private LinearPipeline(List<ReadPipe<? super T>> readPipes, List<WritePipe<? super T>> writePipes){
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
     * Build a LinearPipeline using a LinearPipelineBuilder
     * @return A builder to continue chaining objects to.
     */
    public static <T> LinearPipelineBuilder<T> create(){
        return new LinearPipelineBuilder<>();
    }

    /**
     * A builder class which creates a LinearPipeline
     * @param <B> The type going through the pipeline
     */
    public static class LinearPipelineBuilder<B> extends PipelineBuilder<LinearPipelineBuilder<B>, LinearPipeline<B>, ReadPipe<? super B>, WritePipe<? super B>>{

        @Override
        protected LinearPipelineBuilder<B> self() {
            return this;
        }

        @Override
        public LinearPipeline<B> build() {
            return new LinearPipeline<>(readers, writers);
        }

        /**
         * Creates a ForEach pipeline
         * @param forEachFunction The function to evaluate into the input object into a stream of sub-objects
         * @param <O> The type output by the stream
         * @return A ForEachPipelineBuilder to build out the for-each pipeline.
         */
        public <O> ForEachPipelineBuilder<O> forEach(Function<? super B, Stream<? extends O>> forEachFunction){
            return new ForEachPipelineBuilder<>(this, forEachFunction);
        }

        /**
         * Builds a for-each pipeline
         * @param <O> The type processed by the stream
         */
        public class ForEachPipelineBuilder<O> extends PipelineBuilder<
                ForEachPipelineBuilder<O>,
                LinearPipelineBuilder<B>,
                ReadPipe<? super O>,
                WritePipe<? super O>>{

            private final LinearPipelineBuilder<B> base;
            private Function<? super B, Stream<? extends O>> forEachFunction;

            private ForEachPipelineBuilder(LinearPipelineBuilder<B> base, Function<? super B, Stream<? extends O>> forEachFunction){
                this.base = base;
                this.forEachFunction = forEachFunction;
            }

            @Override
            protected ForEachPipelineBuilder<O> self() {
                return this;
            }

            @Override
            public LinearPipelineBuilder<B> build() {
                Pipeline<O> pipeline = new LinearPipeline<>(this.readers, this.writers);
                base.readers.add(new ForEachPipe<>(this.forEachFunction, pipeline));
                base.writers.add(new ForEachPipe<>(this.forEachFunction, pipeline));
                return base;
            }
        }
    }
}
