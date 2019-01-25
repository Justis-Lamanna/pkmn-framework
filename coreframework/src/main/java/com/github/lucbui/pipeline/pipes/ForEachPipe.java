package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.*;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A pipe which iterates over an object's properties, and placing each through a sub pipeline
 * @param <O> The input object type
 * @param <S> The output object type
 */
public class ForEachPipe<O, S> implements DoublePipe<O> {

    private Function<? super O, Stream<? extends S>> converter;
    private Pipeline<? super S> subPipeline;

    /**
     * Define a ForEachPipe
     * @param converter The function which takes the incoming object, and converts it into a stream of sub-objects
     * @param subPipeline The pipeline to run each sub-object through.
     */
    public ForEachPipe(Function<? super O, Stream<? extends S>> converter, Pipeline<S> subPipeline){
        this.converter = Objects.requireNonNull(converter);
        this.subPipeline = Objects.requireNonNull(subPipeline);
    }

    /**
     * Extract a stream of subobjects from an object, and run each through the sub-pipeline.
     * During iteration, each run through the sub-pipeline gets its own copy of the iterator passed into this
     * pipeline. This prevents "cross-contamination" between subpipes, which would cause hard-to-find bugs.
     * @param object The object to modify
     * @param iterator The iterator to read from
     * @param pkmnFramework The PkmnFramework running this code
     */
    @Override
    public void read(O object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        converter.apply(object).forEach(i -> subPipeline.modify(iterator.copy(), i, pkmnFramework));
    }

    /**
     * Write a stream of sub-objects from an object, and run each through the sub-pipeline.
     * During iteration, each run through the sub-pipeline gets its own copy of the iterator passed into this
     * pipeline. This prevents "cross-contamination" between subpipes, which would cause hard-to-find bugs.
     * @param iterator The iterator to read from
     * @param object The object to modify
     * @param pkmnFramework The PkmnFramework running this code
     */
    @Override
    public void write(HexFieldIterator iterator, O object, PkmnFramework pkmnFramework) {
        converter.apply(object).forEach(i -> subPipeline.write(iterator.copy(), i, pkmnFramework));
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("(");
        sb.append("ForEach -> ").append(subPipeline).append(")");
        return sb.toString();
    }

    /**
     * Initialize a builder for a ForEachPipe
     * @param forEachFunction The function to iterate over
     * @param <O> The input type
     * @param <S> The output type
     * @return A Builder to continue creating the function
     */
    public static <O, S> Builder<O, S> create(Function<? super O, Stream<? extends S>> forEachFunction){
        return new Builder<>(forEachFunction);
    }

    public static class Builder<O, S> extends PipelineBuilder<Builder<O, S>, ForEachPipe<O, S>, ReadPipe<? super S>, WritePipe<? super S>> {
        private Function<? super O, Stream<? extends S>> converter;

        private Builder(Function<? super O, Stream<? extends S>> converter){
            this.converter = converter;
        }

        @Override
        protected Builder<O, S> self() {
            return this;
        }

        @Override
        public ForEachPipe<O, S> build() {
            LinearPipeline<S> pipeline = new LinearPipeline<>(this.readers, this.writers);
            return new ForEachPipe<>(converter, pipeline);
        }

        /**
         * Attach a doublepipe
         * @param doublePipe The doublepipe to use
         * @return
         */
        public Builder<O, S> pipe(DoublePipe<? super S> doublePipe){
            this.readers.add(doublePipe);
            this.writers.add(doublePipe);
            return self();
        }
    }
}
