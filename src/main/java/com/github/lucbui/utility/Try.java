package com.github.lucbui.utility;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class that encapsulates the success, or failure, of an operation
 * @param <T> The type
 */
public final class Try<T> {
    private T object;
    private String errorCause;

    private Try(T object){
        this.object = object;
    }

    private Try(String cause){
        this.errorCause = cause;
    }

    /**
     * Create a valid Try.
     * @param object A non-null result of an object
     * @param <T> The type of result
     * @return The constructed Try
     */
    public static <T> Try<T> ok(T object){
        Objects.requireNonNull(object);
        return new Try<>(object);
    }

    /**
     * Create an invalid try
     * @param <T> The type of result
     * @return The constructed try
     */
    public static <T> Try<T> error(String cause){
        return new Try<>(cause);
    }

    /**
     * Check if this object is okay
     * @return True if object is okay
     */
    public boolean isOk(){
        return object != null;
    }

    /**
     * Check if this object is an error
     * @return True if object is an error
     */
    public boolean isError(){
        return errorCause != null;
    }

    /**
     * Get the result, if it exists
     * @return The object
     */
    public T get(){
        if(isError()){
            throw new IllegalArgumentException("Accessed error Try with get()");
        }
        return object;
    }

    /**
     * Throw an exception if this is an error Try
     * @param throwable A function that converts the error cause to a throwable
     * @param <EX> The exception type
     * @throws EX The exception thrown
     */
    public <EX extends Throwable> void throww(Function<String, EX> throwable) throws EX {
        if(isError()){
            throw throwable.apply(errorCause);
        }
    }

    /**
     * Throw an exception if this is an error Try, or return the contained object
     * @param throwable A function that converts the error cause to a throwable
     * @param <EX> The exception type
     * @return The contained object, if present
     * @throws EX The exception thrown
     */
    public <EX extends Throwable> T or(Function<String, EX> throwable) throws EX{
        throww(throwable);
        return object;
    }

    /**
     * Throw a generic runtime exception if this is an error, otherwise return
     * @return The object encased.
     */
    public T orThrowException(){
        if(isError()){
            throw new RuntimeException(errorCause);
        }
        return object;
    }
}
