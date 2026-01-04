package com.rudsi.paymtool.error;

/**
 * Exception thrown when decryption operations fail.
 * <p>
 * This typically indicates that the encrypted payload is malformed,
 * corrupted, or was encrypted with a different key than expected.
 */
public class DecryptionException extends RuntimeException {

    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

