package com.github.lucbui.pipeline.exceptions;

/**
 * Indicates an exception throw at Create Pipe time
 */
public class CreatePipeException extends RuntimeException{
    public CreatePipeException() {
    }

    public CreatePipeException(String message) {
        super(message);
    }

    public CreatePipeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreatePipeException(Throwable cause) {
        super(cause);
    }

    public CreatePipeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
