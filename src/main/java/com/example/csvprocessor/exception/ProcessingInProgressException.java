package com.example.csvprocessor.exception;


public class ProcessingInProgressException extends RuntimeException {
    public ProcessingInProgressException(String message) {
        super(message);
    }
}