package com.github.lucbui.pipeline;

import com.github.lucbui.pipeline.pipes.ForEachPipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Holy shit what even is this
 * @param <SELF> The implementor of the abstract class
 * @param <BUILD> The object being built
 * @param <READPIPE> The objects which make up the read pipe.
 * @param <WRITEPIPE> The objects which make up the write pipe.
 */
public abstract class PipelineBuilder<
        SELF,
        BUILD,
        READPIPE,
        WRITEPIPE> {
    protected List<READPIPE> readers;
    protected List<WRITEPIPE> writers;

    protected PipelineBuilder(){
        this.readers = new ArrayList<>();
        this.writers = new ArrayList<>();
    }

    protected abstract SELF self();

    public SubPipeBuilder<READPIPE> read(READPIPE pipe){
        Objects.requireNonNull(pipe);
        return new SubPipeBuilder<>(self(), readers).then(pipe);
    }

    public SubPipeBuilder<WRITEPIPE> write(WRITEPIPE pipe){
        Objects.requireNonNull(pipe);
        return new SubPipeBuilder<>(self(), writers).then(pipe);
    }

    public abstract BUILD build();

    public class SubPipeBuilder<SUBPIPE> {
        protected SELF baseBuilder;
        protected List<SUBPIPE> oldPipe;
        protected List<SUBPIPE> newPipe;

        /**
         * Initialize a SubPipelineBuilder
         * @param base The base builder to use
         */
        protected SubPipeBuilder(SELF base, List<SUBPIPE> parentPipe){
            this.baseBuilder = base;
            this.oldPipe = parentPipe;
            this.newPipe = new ArrayList<>();
        }

        /**
         * Add a pipe to this builder
         * @param pipe The pipe to add
         * @return This instance
         */
        public SubPipeBuilder<SUBPIPE> then(SUBPIPE pipe){
            newPipe.add(pipe);
            return this;
        }

        /**
         * Wrap up building and return the base builder back
         * @return The base builder instance
         */
        public SELF end(){
            oldPipe.clear();
            oldPipe.addAll(newPipe);
            return baseBuilder;
        }
    }
}
