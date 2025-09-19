package com.example.csvprocessor.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {

    @Test
    void testInvalidFileExceptionMessage() {
        InvalidFileException ex = new InvalidFileException("Invalid file");
        assertEquals("Invalid file", ex.getMessage());
    }

    @Test
    void testProcessingInProgressExceptionMessage() {
        ProcessingInProgressException ex = new ProcessingInProgressException("Still processing");
        assertEquals("Still processing", ex.getMessage());
    }
}
