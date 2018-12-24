package com.github.lucbui.utility;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Utility collector to convert a list of optionals into an optional of a list.
 * If a list contains any empty optionals, the resulting conversion gives an empty optional. Else, it yields
 * an optional of all the containing values
 * @param <T>
 */
public class ListOptionalToOptionalListCollector<T> implements Collector<Optional<T>, ListOptionalToOptionalListCollector.ListWithFlag<T>, Optional<List<T>>> {

    @Override
    public Supplier<ListWithFlag<T>> supplier() {
        return ListWithFlag::new;
    }

    @Override
    public BiConsumer<ListWithFlag<T>, Optional<T>> accumulator() {
        return ListWithFlag::add;
    }

    @Override
    public BinaryOperator<ListWithFlag<T>> combiner() {
        return ListWithFlag::merge;
    }

    @Override
    public Function<ListWithFlag<T>, Optional<List<T>>> finisher() {
        return ListWithFlag::finish;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    /**
     * An intermediary between the List of Optionals and the Optional of List.
     * If a non-empty optional is "added", its contents are added instead. If an empty
     * optional is added, the hasEmpty flag is set, and no more additions occur.
     * @param <R>
     */
    static class ListWithFlag<R>{

        private boolean hasEmpty;
        private List<R> list;

        private ListWithFlag(){
            hasEmpty = false;
            list = new ArrayList<>();
        }

        /**
         * Add to the list
         * If an empty optional is added, the internal list is cleared, and the hasEmpty flag is set.
         * No further additions will occur.
         * @param optional
         */
        public void add(Optional<R> optional){
            if(!hasEmpty) {
                if (optional.isPresent()) {
                    list.add(optional.get());
                } else {
                    hasEmpty = true;
                    list.clear();
                }
            }
        }

        /**
         * Merge another ListWithFlag to this one.
         * @param toMerge
         * @return
         */
        public ListWithFlag<R> merge(ListWithFlag<R> toMerge){
            ListWithFlag<R> merged = new ListWithFlag<>();
            merged.hasEmpty = this.hasEmpty || toMerge.hasEmpty;
            if(!merged.hasEmpty){
                merged.list.addAll(this.list);
                merged.list.addAll(toMerge.list);
            }
            return merged;
        }

        /**
         * Converts this list to an Optional of Lists
         * @return
         */
        public Optional<List<R>> finish(){
            if(this.hasEmpty){
                return Optional.empty();
            } else {
                return Optional.of(this.list);
            }
        }
    }
}
