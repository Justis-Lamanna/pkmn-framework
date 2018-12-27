package com.github.lucbui.pipeline.exceptions;

/**
 * Indicates an exception thrown at ReadPipe time
 */
public class ReadPipeException extends RuntimeException {
    public ReadPipeException() {
    }

    public ReadPipeException(String message) {
        super(message);
    }

    public ReadPipeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadPipeException(Throwable cause) {
        super(cause);
    }

    public ReadPipeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
