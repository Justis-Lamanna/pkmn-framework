package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.Pipeline;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.WritePipe;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class ForEachPipe<O, S> implements ReadPipe<O>, WritePipe<O> {

    private Function<? super O, Stream<? extends S>> converter;
    private Pipeline<? super S> subPipeline;

    public ForEachPipe(Function<? super O, Stream<? extends S>> converter, Pipeline<S> subPipeline){
        this.converter = Objects.requireNonNull(converter);
        this.subPipeline = Objects.requireNonNull(subPipeline);
    }

    @Override
    public void read(O object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        converter.apply(object).forEach(i -> subPipeline.modify(iterator, i, pkmnFramework));
    }

    @Override
    public void write(HexFieldIterator iterator, O object, PkmnFramework pkmnFramework) {
        converter.apply(object).forEach(i -> subPipeline.write(iterator, i, pkmnFramework));
    }
}
