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

    /**
     * Returns the implementor.
     * This should simply returns "this".
     * @return This object.
     */
    protected abstract SELF self();

    /**
     * Create the built object.
     * @return The built object BUILD.
     */
    public abstract BUILD build();

    /**
     * Build out the READ subpipe
     * @param pipe The first pipe in the subpipe
     * @return This instance for further chaining
     */
    public SELF read(READPIPE pipe){
        Objects.requireNonNull(pipe);
        this.readers.add(pipe);
        return self();
    }

    /**
     * Build out the WRITE subpipe
     * @param pipe The first pipe in the subpipe
     * @return This instance for further chaining
     */
    public SELF write(WRITEPIPE pipe){
        Objects.requireNonNull(pipe);
        this.writers.add(pipe);
        return self();
    }

    /**
     * Add a reader and writer both.
     * @param readPipe THe reader to add
     * @param writePipe The writer to add.
     * @return
     */
    public SELF pipe(READPIPE readPipe, WRITEPIPE writePipe){
        Objects.requireNonNull(readPipe);
        Objects.requireNonNull(writePipe);
        this.readers.add(readPipe);
        this.writers.add(writePipe);
        return self();
    }
}
