package com.example.csvprocessor.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * <p>
 * Commonly used for missing files, database records, or invalid identifiers.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified error message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}