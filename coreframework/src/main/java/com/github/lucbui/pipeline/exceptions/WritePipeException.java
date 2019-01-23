package com.github.lucbui.pipeline.exceptions;

public class WritePipeException extends RuntimeException {
    public WritePipeException() {
    }

    public WritePipeException(String message) {
        super(message);
    }

    public WritePipeException(String message, Throwable cause) {
        super(message, cause);
    }

    public WritePipeException(Throwable cause) {
        super(cause);
    }

    public WritePipeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
