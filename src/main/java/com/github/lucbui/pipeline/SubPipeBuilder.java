package com.github.lucbui.pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * A sub-builder which builds out subpipes of a pipeline
 * @param <PARENT> The parent type
 * @param <SUBPIPE> The subpipe being modified.
 */
public class SubPipeBuilder<PARENT, SUBPIPE> {
    protected PARENT baseBuilder;
    protected List<SUBPIPE> oldPipe;
    protected List<SUBPIPE> newPipe;

    /**
     * Initialize a SubPipelineBuilder
     * @param base The base builder to use
     */
    protected SubPipeBuilder(PARENT base, List<SUBPIPE> parentPipe){
        this.baseBuilder = base;
        this.oldPipe = parentPipe;
        this.newPipe = new ArrayList<>();
    }

    /**
     * Add a pipe to this builder
     * @param pipe The pipe to add
     * @return This instance
     */
    public SubPipeBuilder<PARENT, SUBPIPE> then(SUBPIPE pipe){
        newPipe.add(pipe);
        return this;
    }

    /**
     * Wrap up building and return the base builder back
     * @return The base builder instance
     */
    public PARENT end(){
        oldPipe.clear();
        oldPipe.addAll(newPipe);
        return baseBuilder;
    }
}
