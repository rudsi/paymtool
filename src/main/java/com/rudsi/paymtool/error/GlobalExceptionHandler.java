package com.rudsi.paymtool.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.Instant;

/**
 * Global exception handler for the application.
 * <p>
 * This class provides centralized error handling across all REST controllers,
 * converting exceptions into appropriate HTTP responses with consistent error
 * structures. It handles validation errors, business logic exceptions, and
 * unexpected system errors gracefully.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors from request body validation.
     *
     * @param ex the validation exception
     * @return HTTP 400 response with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        logger.warn("Validation error: {}", message);

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                message,
                "INVALID_REQUEST",
                Instant.now().toEpochMilli());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles bind exceptions from request parameter validation.
     *
     * @param ex the bind exception
     * @return HTTP 400 response with validation error details
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        logger.warn("Bind exception: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                "Invalid request parameters",
                "INVALID_REQUEST",
                Instant.now().toEpochMilli());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles illegal argument exceptions (e.g., invalid card number format).
     *
     * @param ex the illegal argument exception
     * @return HTTP 400 response with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                ex.getMessage(),
                "INVALID_INPUT",
                Instant.now().toEpochMilli());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles decryption failures.
     *
     * @param ex the decryption exception
     * @return HTTP 400 response indicating decryption failure
     */
    @ExceptionHandler(DecryptionException.class)
    public ResponseEntity<ErrorResponse> handleDecryptionException(DecryptionException ex) {
        logger.error("Decryption failed: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                "ERROR",
                "Failed to decrypt the request payload. The data may be corrupted or encrypted with a different key.",
                "DECRYPTION_FAILED",
                Instant.now().toEpochMilli());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles encryption failures.
     *
     * @param ex the encryption exception
     * @return HTTP 500 response indicating encryption failure
     */
    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<ErrorResponse> handleEncryptionException(EncryptionException ex) {
        logger.error("Encryption failed: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                "ERROR",
                "Failed to encrypt data. Please contact support.",
                "ENCRYPTION_FAILED",
                Instant.now().toEpochMilli());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handles data processing failures (e.g., JSON parsing errors).
     *
     * @param ex the data processing exception
     * @return HTTP 400 response indicating data processing failure
     */
    @ExceptionHandler(DataProcessingException.class)
    public ResponseEntity<ErrorResponse> handleDataProcessingException(DataProcessingException ex) {
        logger.error("Data processing failed: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                "ERROR",
                "Failed to process the request data. The payload format may be invalid.",
                "DATA_PROCESSING_FAILED",
                Instant.now().toEpochMilli());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles persistence failures.
     *
     * @param ex the persistence exception
     * @return HTTP 500 response indicating persistence failure
     */
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handlePersistenceException(PersistenceException ex) {
        logger.error("Persistence failed: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                "ERROR",
                "Failed to persist data. Please try again later.",
                "PERSISTENCE_FAILED",
                Instant.now().toEpochMilli());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handles JSON processing exceptions.
     *
     * @param ex the JSON processing exception
     * @return HTTP 400 response indicating JSON parsing failure
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
        logger.warn("JSON processing error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "ERROR",
                "Invalid JSON format in request payload",
                "INVALID_JSON",
                Instant.now().toEpochMilli());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles all other unexpected exceptions.
     *
     * @param ex the unexpected exception
     * @return HTTP 500 response with generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        ErrorResponse error = new ErrorResponse(
                "ERROR",
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_ERROR",
                Instant.now().toEpochMilli());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /***
     * 
     * @param ex
     * @return HTTP 500 response with "Database operation failed" message
     */

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseErrors(DataAccessException ex){
        logger.error("Database error occured", ex);
        ErrorResponse error = new ErrorResponse(
            "Error",
            "Database operation failed",
            "Internal Error",
            Instant.now().toEpochMilli());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

