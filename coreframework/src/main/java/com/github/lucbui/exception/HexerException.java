package com.github.lucbui.exception;

/**
 * An exception thrown when running a hexer
 */
public class HexerException extends RuntimeException {
    public HexerException() {
    }

    public HexerException(String message) {
        super(message);
    }

    public HexerException(String message, Throwable cause) {
        super(message, cause);
    }

    public HexerException(Throwable cause) {
        super(cause);
    }

    public HexerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
