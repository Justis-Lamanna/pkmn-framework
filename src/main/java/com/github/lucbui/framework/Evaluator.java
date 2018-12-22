package com.github.lucbui.framework;

import com.github.lucbui.utility.ParseUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;

/**
 * An object which evaluates a string, returning a result string.
 */
public interface Evaluator {

    /**
     * Evaluate the string
     * @param evaluation The string to evaluate
     * @return The result of the evaluation
     */
    String evaluate(String evaluation);

    /**
     * Evaluate the string as a long
     * @param evaluation The string to evaluate
     * @return The value, parsed as a long, or an empty optional if it could not be parsed.
     */
    default OptionalLong evaluateLong(String evaluation){
        return ParseUtils.parseLong(evaluate(evaluation), 10);
    }

    /**
     * Evaluate the string as an integer
     * @param evaluation The string to evaluate
     * @return The value, parsed as an int, or an empty optional if it could not be parsed.
     */
    default OptionalInt evaluateInt(String evaluation){
        return ParseUtils.parseInt(evaluate(evaluation), 10);
    }

    /**
     * Evaluate the string as an object
     * @param evaluation The string to evaluate
     * @param converter The converter to turn the result string into a value
     * @param <T> The type being converted into
     * @return The value, parsed as the specified object, or an empty optional if null was returned
     */
    default <T> Optional<T> evaluateObject(String evaluation, Function<String, T> converter){
        Objects.requireNonNull(converter);
        return Optional.ofNullable(converter.apply(evaluate(evaluation)));
    }
}
