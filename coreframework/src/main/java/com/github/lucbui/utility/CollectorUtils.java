package com.github.lucbui.utility;

import com.github.lucbui.utility.collectors.ToPrimitiveArrayCollector;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.IntStream;

/**
 * Collector-based utilities
 */
public class CollectorUtils {
    private CollectorUtils(){
        //
    }

    /**
     * Collects a List of Optionals into an Optional of a List.
     * If any Optional in the list is empty, an empty optional is returned. Else,
     * an optional containing the unwrapped list is returned
     * @param <T> The type in the optional
     * @return A collector that turns a List of Optionals into an Optional of List.
     */
    public static <T> Collector<Optional<T>, ?, Optional<List<T>>> toOptionalList(){
        return new ListOptionalToOptionalListCollector<>();
    }

    /**
     * Collects a Byte list into a byte array.
     * @return A collector that turns a List of Bytes into a primitive byte array
     */
    public static Collector<Byte, ?, byte[]> toByteArray(){
        return ToPrimitiveArrayCollector.BYTE;
    }
}
