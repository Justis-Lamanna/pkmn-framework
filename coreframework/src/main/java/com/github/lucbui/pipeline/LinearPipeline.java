package com.github.lucbui.pipeline;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates a linear read-write pipeline for populating objects.
 * Objects simply travel in a straight line, through either the readPipe or the writePipe. No branching
 * or early pipe termination can be used with this; thus, it is the simplest type of pipeline.
 */
public class LinearPipeline<T> implements Pipeline<T> {
    private List<ReadPipe<? super T>> readPipes;
    private List<WritePipe<? super T>> writePipes;

    public LinearPipeline(List<ReadPipe<? super T>> readPipes, List<WritePipe<? super T>> writePipes){
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
     * Debug-friendly string for displaying pipelines
     * @return The pipeline as a string.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("READ:\n")
            .append("\t")
                .append(readPipes.stream().map(Object::toString).collect(Collectors.joining("->\n\t")))
            .append("\nWRITE:\n")
            .append("\t")
                .append(writePipes.stream().map(Object::toString).collect(Collectors.joining("->\n\t")));
        return sb.toString();
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
         * Attach a doublepipe
         * @param doublePipe The doublepipe to use
         * @return
         */
        public LinearPipelineBuilder<B> pipe(DoublePipe<? super B> doublePipe){
            this.readers.add(doublePipe);
            this.writers.add(doublePipe);
            return self();
        }
    }
}
