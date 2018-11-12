package com.github.lucbui.gba.exception;

public class IllegalSizeException extends RuntimeException {
    public IllegalSizeException() {
    }

    public IllegalSizeException(String message) {
        super(message);
    }

    public IllegalSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalSizeException(Throwable cause) {
        super(cause);
    }

    public IllegalSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
