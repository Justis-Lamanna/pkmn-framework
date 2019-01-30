package com.github.lucbui.utility;

import java.util.Objects;
import java.util.function.*;

/**
 * Class that encapsulates the success, or failure, of an operation.
 * Based on the same type in Scala. Functions similarly to an Optional, but also
 * contains error information. Try can also contain nulls, if desired.
 *
 * The difference between the two is one of intent. While Optional is meant to replace
 * nulls, Try is meant to encapsulate Try/Catch blocks, and omit the concept of checked exceptions.
 * For instance, instead of throwing an IOException when writing fails, a Try could be returned instead.
 * @param <T> The type
 */
public final class Try<T> {
    private T object;
    private String errorCause;
    private Exception exception;

    private Try(T object){
        this.object = object;
    }

    private Try(String cause, Exception ex){
        this.errorCause = cause;
        this.exception = ex;
    }

    /**
     * Try a given supplier, returning the result if successful, and an error Try if unsuccessful
     * @param tryFunc The function to try running
     * @param errorCause The cause of the error, if one exists
     * @param <T> The type contained in the Try
     * @return A Try corresponding to what happened in the tryFunc.
     */
    public static <T> Try<T> running(SupplierWithException<T> tryFunc, String errorCause) {
        try{
            T value = tryFunc.get();
            if(value != null) {
                return new Try<>(value);
            } else {
                return new Try<>(null);
            }
        } catch (Exception ex){
            return new Try<>(errorCause, ex);
        }
    }

    /**
     * Create a valid Try.
     * @param object A non-null result of an object
     * @param <T> The type of result
     * @return The constructed Try
     */
    public static <T> Try<T> ok(T object){
        return new Try<>(object);
    }

    /**
     * Create a valid Try
     * @param <T> The type of result
     * @return The constructed try
     */
    public static <T> Try<T> ok(){
        return new Try<>(null);
    }

    /**
     * Create an invalid try
     * @param <T> The type of result
     * @return The constructed try
     */
    public static <T> Try<T> error(String cause, Exception ex){
        return new Try<>(cause, ex);
    }

    /**
     * Create an invalid try
     * @param cause The cause of error
     * @param <T> The type expected by the result
     * @return The constructed try
     */
    public static <T> Try<T> error(String cause){
        return new Try<>(cause, new TryException(cause));
    }

    /**
     * Create an invalid try
     * @param ex The exception thrown
     * @param <T> The Try wrapped type expected
     * @return The constructed try
     */
    public static <T> Try<T> error(Exception ex){
        return new Try<>("Error try encountered", ex);
    }

    /**
     * Check if this object is okay
     * @return True if object is okay
     */
    public boolean isOk(){
        return errorCause == null;
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
            throw new TryException("Accessed error Try with get()");
        }
        return object;
    }

    public String getCause(){
        if(isOk()){
            throw new TryException("Accessed OK Try with getCause()");
        }
        return errorCause;
    }

    public Exception getException(){
        if(isOk()){
            throw new TryException("Accessed OK Try with getException()");
        }
        return exception;
    }

    /**
     * Throw an exception if this is an error Try, or return the contained object
     * @param throwable A function that converts the error cause to a throwable
     * @param <EX> The exception type
     * @return The contained object, if present
     * @throws EX The exception thrown
     */
    public <EX extends Throwable> T orThrow(BiFunction<String, ? super Exception, EX> throwable) throws EX{
        if(isError()){
            throw throwable.apply(errorCause, exception);
        }
        return object;
    }

    /**
     * If this is an error Try, throw a RuntimeException wrapping the errorCause and exception
     * @return The object contained
     * @throws RuntimeException Thrown if this is a error Try
     */
    public T orThrow() throws RuntimeException {
        if(isError()){
            if(exception instanceof RuntimeException){
                throw (RuntimeException)exception;
            } else {
                throw new RuntimeException(errorCause, exception);
            }
        }
        return object;
    }

    /**
     * Map the contained object into a new Try
     * @param function The function to turn the internal object into a new one
     * @param <R> The type of the new object
     * @return A new Try, containing the mapped object, or the same error.
     */
    public <R> Try<R> map(Function<? super T, R> function){
        Objects.requireNonNull(function);
        if(isOk()){
            return new Try<>(function.apply(object));
        } else {
            return new Try<>(errorCause, exception);
        }
    }

    /**
     * Execute the supplied consumer if an object is present
     * @param consumer The function which consumes the internal object
     */
    public void ifPresent(Consumer<? super T> consumer){
        Objects.requireNonNull(consumer);
        if(isOk()){
            consumer.accept(object);
        }
    }

    /**
     * Return the internal object, or a default if this is an error
     * @param defaultObject The default object
     * @return The internal object, or the default object in case of error.
     */
    public T or(T defaultObject){
        if(isError()){
            return defaultObject;
        }
        return object;
    }

    /**
     * Return the internal object, or call the provided supplier if this is an error
     * @param supplier The object supplier
     * @return The internal object, or the result of a call to the provided supplier.
     */
    public T orGet(Supplier<? extends T> supplier){
        if(isError()){
            return supplier.get();
        }
        return object;
    }

    private static class TryException extends RuntimeException{
        public TryException() {
        }

        public TryException(String message) {
            super(message);
        }

        public TryException(String message, Throwable cause) {
            super(message, cause);
        }

        public TryException(Throwable cause) {
            super(cause);
        }

        public TryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
