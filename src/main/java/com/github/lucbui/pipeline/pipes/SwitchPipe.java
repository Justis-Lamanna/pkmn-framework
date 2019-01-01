package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.DoublePipe;
import com.github.lucbui.pipeline.Pipeline;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.WritePipe;

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
    public void read(T object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        for(SwitchCase<? super T> caze : cases){
            if(caze.predicate.test(object)){
                caze.pipeline.modify(iterator, object, pkmnFramework);
                break;
            }
        }
    }

    @Override
    public void write(HexFieldIterator iterator, T object, PkmnFramework pkmnFramework) {
        for(SwitchCase<? super T> caze : cases){
            if(caze.predicate.test(object)){
                caze.pipeline.write(iterator, object, pkmnFramework);
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

        public SwitchPipe<T> elsee(Pipeline<? super T> pipeline){
            this.cases.add(new SwitchCase<>(pipeline));
            return end();
        }
    }

    public static class BuilderIf<T>{
        private Builder<T> baseBuilder;
        private Predicate<? super T> condition;

        BuilderIf(Builder<T> baseBuilder, Predicate<? super T> condition) {
            this.baseBuilder = baseBuilder;
            this.condition = condition;
        }

        public Builder<T> then(Pipeline<? super T> pipeline){
            Objects.requireNonNull(pipeline);
            baseBuilder.cases.add(new SwitchCase<>(condition, pipeline));
            return baseBuilder;
        }
    }
}
