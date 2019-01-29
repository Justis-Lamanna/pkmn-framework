package com.github.lucbui.utility.collectors;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

public abstract class ToPrimitiveArrayCollector<ARRAY> implements Collector<Byte, List<Byte>, ARRAY> {
    @Override
    public Supplier<List<Byte>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<Byte>, Byte> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<Byte>> combiner() {
        return (a, b) -> {
            List<Byte> combined = new ArrayList<>(a);
            combined.addAll(b);
            return combined;
        };
    }
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }
}
