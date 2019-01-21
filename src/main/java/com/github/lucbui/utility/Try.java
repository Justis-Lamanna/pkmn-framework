package com.github.lucbui.utility;

import java.util.function.Function;

/**
 * Class that encapsulates the success, or failure, of an operation
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
            throw new IllegalArgumentException("Accessed error Try with get()");
        }
        return object;
    }

    /**
     * Throw an exception if this is an error Try, or return the contained object
     * @param throwable A function that converts the error cause to a throwable
     * @param <EX> The exception type
     * @return The contained object, if present
     * @throws EX The exception thrown
     */
    public <EX extends Throwable> T or(Function<String, EX> throwable) throws EX{
        if(isError()){
            throw throwable.apply(errorCause);
        }
        return object;
    }

    /**
     * Throw the enclosed exception if this is an error, else return the enclosed object
     * @return
     * @throws Exception
     */
    public T orThrow() throws Exception{
        if(isError()){
            throw exception;
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
