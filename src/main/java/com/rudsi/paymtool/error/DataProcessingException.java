package com.rudsi.paymtool.error;

/**
 * Exception thrown when data processing operations fail.
 * <p>
 * This includes JSON parsing errors, data transformation failures,
 * or other processing-related issues.
 */
public class DataProcessingException extends RuntimeException {

    public DataProcessingException(String message) {
        super(message);
    }

    public DataProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

