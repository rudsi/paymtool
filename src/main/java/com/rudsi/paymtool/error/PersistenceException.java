package com.rudsi.paymtool.error;

/**
 * Exception thrown when database persistence operations fail.
 * <p>
 * This typically indicates a problem with database connectivity,
 * constraint violations, or other persistence-related issues.
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}

