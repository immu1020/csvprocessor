package com.example.csvprocessor.exception;

/**
 * Exception thrown when a file is already being processed and cannot be handled concurrently.
 * <p>
 * Useful for preventing duplicate or overlapping processing tasks on the same resource.
 */
public class ProcessingInProgressException extends RuntimeException {

    /**
     * Constructs a new ProcessingInProgressException with the specified error message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ProcessingInProgressException(String message) {
        super(message);
    }
}