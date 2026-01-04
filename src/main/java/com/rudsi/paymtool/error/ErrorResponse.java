package com.rudsi.paymtool.error;

/**
 * Standard error response structure for API error handling.
 *
 * @param status    error status code (e.g., "ERROR", "VALIDATION_ERROR")
 * @param message   human-readable error message
 * @param errorCode specific error code for programmatic handling
 * @param timestamp timestamp when the error occurred
 */
public record ErrorResponse(
        String status,
        String message,
        String errorCode,
        long timestamp) {
}

