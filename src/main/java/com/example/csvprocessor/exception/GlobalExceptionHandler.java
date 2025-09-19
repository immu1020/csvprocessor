package com.example.csvprocessor.exception;

import com.example.csvprocessor.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

/**
 * Centralized exception handler for the application.
 * <p>
 * Captures and formats exceptions into consistent API responses.
 * Uses {@link ApiResponse} for structured error messaging.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link ResourceNotFoundException} when a requested resource is missing.
     *
     * @param ex the exception thrown
     * @return BAD_REQUEST response with error message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(ex.getMessage(), 400, null), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link IllegalArgumentException} for invalid input or parameters.
     *
     * @param ex the exception thrown
     * @return BAD_REQUEST response with error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ApiResponse<>(ex.getMessage(), 400, null), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link IllegalStateException} when a resource is locked or in an invalid state.
     *
     * @param ex the exception thrown
     * @return LOCKED response with error message
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalState(IllegalStateException ex) {
        return new ResponseEntity<>(new ApiResponse<>(ex.getMessage(), 423, null), HttpStatus.LOCKED);
    }

    /**
     * Handles {@link MaxUploadSizeExceededException} when uploaded file exceeds size limit.
     *
     * @param ex the exception thrown
     * @return PAYLOAD_TOO_LARGE response with predefined error message
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleFileTooLarge(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(new ApiResponse<>("File size exceeds limit", 413, null), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * Handles {@link InvalidFileException} for unsupported or malformed files.
     *
     * @param ex the exception thrown
     * @return BAD_REQUEST response with error message
     */
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<?> handleInvalidFile(InvalidFileException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles {@link ProcessingInProgressException} when a file is already being processed.
     *
     * @param ex the exception thrown
     * @return LOCKED response with error message
     */
    @ExceptionHandler(ProcessingInProgressException.class)
    public ResponseEntity<?> handleProcessing(ProcessingInProgressException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED).body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles all uncaught exceptions and logs them.
     *
     * @param ex the exception thrown
     * @return INTERNAL_SERVER_ERROR response with generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAll(Exception ex) {
        logger.error("Unhandled exception", ex);
        return new ResponseEntity<>(new ApiResponse<>("Internal server error", 500, null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
