package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.HexFramework;
import com.github.lucbui.pipeline.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A pipe which goes down one of several pipelines depending on the outcome of a Predicate
 * @param <T>
 */
public class SwitchPipe<T> implements DoublePipe<T> {

    private List<SwitchCase<? super T>> cases;

    private SwitchPipe(List<SwitchCase<? super T>> cases){
        this.cases = cases;
    }

    @Override
    public void read(T object, HexFieldIterator iterator, HexFramework hexFramework) {
        for(SwitchCase<? super T> caze : cases){
            if(caze.predicate.test(object)){
                caze.pipeline.modify(iterator, object, hexFramework);
                break;
            }
        }
    }

    @Override
    public void write(HexFieldIterator iterator, T object, HexFramework hexFramework) {
        for(SwitchCase<? super T> caze : cases){
            if(caze.predicate.test(object)){
                caze.pipeline.write(iterator, object, hexFramework);
                break;
            }
        }
    }

    /**
     * Create a SwitchPipe through a Builder
     * @param <T>
     * @return
     */
    public static <T> SwitchPipe.Builder<T> create(){
        return new SwitchPipe.Builder<>();
    }

    /**
     * Create a SwitchPipe through a Builder
     * @param predicate The predicate to use
     * @param <T> The type to test on
     * @return A Builder to continue creating the SwitchPipe
     */
    public static <T> SwitchPipe.BuilderIf<T> iff(Predicate<? super T> predicate){
        return new SwitchPipe.BuilderIf<>(new SwitchPipe.Builder<>(), predicate);
    }

    /**
     * A class that encapsulates a predicate and its corresponding pipeline.
     * @param <T>
     */
    private static class SwitchCase<T> {
        private Predicate<? super T> predicate;
        private Pipeline<? super T> pipeline;

        /**
         * Create a SwitchCase
         * @param predicate If this predicate tests true, the pipeline is gone through
         * @param pipeline The pipeline to traverse if the predicate is true
         */
        SwitchCase(Predicate<? super T> predicate, Pipeline<? super T> pipeline) {
            this.predicate = predicate;
            this.pipeline = pipeline;
        }

        /**
         * Create a "default" SwitchCase
         * @param pipeline The pipeline to travel. If this case is hit, it will always travel the pipeline.
         */
        SwitchCase(Pipeline<? super T> pipeline){
            this.predicate = i -> true;
            this.pipeline = pipeline;
        }
    }

    public static class Builder<T>{
        private List<SwitchCase<? super T>> cases;

        private Builder(){
            this.cases = new ArrayList<>();
        }

        public BuilderIf<T> iff(Predicate<? super T> condition){
            Objects.requireNonNull(condition);
            return new BuilderIf<>(this, condition);
        }

        public SwitchPipe<T> end(){
            return new SwitchPipe<>(cases);
        }

        public BuilderIf<T> elsee(){
            return new BuilderIf<>(this, i -> true);
        }
    }

    public static class BuilderIf<T> extends PipelineBuilder<
            BuilderIf<T>,
            Builder<T>,
            ReadPipe<? super T>,
            WritePipe<? super T>> {
        private Builder<T> baseBuilder;
        private Predicate<? super T> condition;

        BuilderIf(Builder<T> baseBuilder, Predicate<? super T> condition) {
            this.baseBuilder = baseBuilder;
            this.condition = condition;
        }

        @Override
        protected BuilderIf<T> self() {
            return this;
        }

        @Override
        public Builder<T> build() {
            LinearPipeline<T> pipeline = new LinearPipeline<>(readers, writers);
            baseBuilder.cases.add(new SwitchCase<>(condition, pipeline));
            return baseBuilder;
        }

        /**
         * Pipe a DoublePipe
         * @param doublePipe The doublepipe to use
         * @return
         */
        public BuilderIf<T> pipe(DoublePipe<? super T> doublePipe){
            return pipe(doublePipe, doublePipe);
        }
    }
}
