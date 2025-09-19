package com.example.csvprocessor.exception;

/**
 * Exception thrown when an uploaded file is invalid or unsupported.
 * <p>
 * Typically used to signal issues like incorrect format, missing content, or disallowed file types.
 */
public class InvalidFileException extends RuntimeException {

    /**
     * Constructs a new InvalidFileException with the specified error message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidFileException(String message) {
        super(message);
    }
}