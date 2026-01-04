package com.rudsi.paymtool.error;

/**
 * Exception thrown when encryption operations fail.
 * <p>
 * This typically indicates a problem with the encryption service
 * configuration or key material.
 */
public class EncryptionException extends RuntimeException {

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

