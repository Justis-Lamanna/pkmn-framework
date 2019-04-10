package com.github.lucbui.utility.collectors;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class ToPrimitiveArrayCollector<BOXED, ARRAY> implements Collector<BOXED, List<BOXED>, ARRAY> {
    private Function<Integer, ARRAY> arrayCreator;
    private TriConsumer<ARRAY, Integer, BOXED> accessArray;

    private ToPrimitiveArrayCollector(Function<Integer, ARRAY> arrayCreator, TriConsumer<ARRAY, Integer, BOXED> accessArray) {
        this.arrayCreator = arrayCreator;
        this.accessArray = accessArray;
    }

    /**
     * A collector that turns a Byte list into a byte array
     */
    public static final ToPrimitiveArrayCollector<Byte, byte[]> BYTE = new ToPrimitiveArrayCollector<>(byte[]::new, (array, idx, val) -> array[idx] = val);

    /**
     * A collector that turns an Integer list into an int array
     */
    public static final ToPrimitiveArrayCollector<Byte, int[]> INT = new ToPrimitiveArrayCollector<>(int[]::new, (array, idx, val) -> array[idx] = val);

    /**
     * A collector that turns a Long list into a long array
     */
    public static final ToPrimitiveArrayCollector<Byte, long[]> LONG = new ToPrimitiveArrayCollector<>(long[]::new, (array, idx, val) -> array[idx] = val);

    /**
     * A collector that turns a Double list into a double array
     */
    public static final ToPrimitiveArrayCollector<Byte, double[]> DOUBLE = new ToPrimitiveArrayCollector<>(double[]::new, (array, idx, val) -> array[idx] = val);

    @Override
    public Supplier<List<BOXED>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<BOXED>, BOXED> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<BOXED>> combiner() {
        return (a, b) -> {
            List<BOXED> combined = new ArrayList<>(a);
            combined.addAll(b);
            return combined;
        };
    }
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    @Override
    public Function<List<BOXED>, ARRAY> finisher() {
        return (list) -> {
            ARRAY array = arrayCreator.apply(list.size());
            IntStream.range(0, list.size())
                    .forEach(idx -> accessArray.apply(array, idx, list.get(idx)));
            return array;
        };
    }

    private interface TriConsumer<A, B, C> {
        void apply(A a, B b, C c);
    }
}
