package com.example.csvprocessor.exception;

import com.example.csvprocessor.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testInvalidFileExceptionMessage() {
        InvalidFileException ex = new InvalidFileException("Invalid file");
        assertEquals("Invalid file", ex.getMessage());

        ResponseEntity<?> response = handler.handleInvalidFile(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("Invalid file", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testProcessingInProgressExceptionMessage() {
        ProcessingInProgressException ex = new ProcessingInProgressException("Still processing");
        assertEquals("Still processing", ex.getMessage());

        ResponseEntity<?> response = handler.handleProcessing(ex);
        assertEquals(HttpStatus.LOCKED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("Still processing", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testResourceNotFoundHandler() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ResponseEntity<ApiResponse<String>> response = handler.handleNotFound(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Not found", response.getBody().getMessage());
        assertEquals(400, response.getBody().getCode());
    }

    @Test
    void testIllegalArgumentHandler() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad input");
        ResponseEntity<ApiResponse<String>> response = handler.handleIllegalArgument(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad input", response.getBody().getMessage());
    }

    @Test
    void testIllegalStateHandler() {
        IllegalStateException ex = new IllegalStateException("Locked state");
        ResponseEntity<ApiResponse<String>> response = handler.handleIllegalState(ex);
        assertEquals(HttpStatus.LOCKED, response.getStatusCode());
        assertEquals("Locked state", response.getBody().getMessage());
    }

    @Test
    void testMaxUploadSizeExceededHandler() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1024);
        ResponseEntity<ApiResponse<String>> response = handler.handleFileTooLarge(ex);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertEquals("File size exceeds limit", response.getBody().getMessage());
    }

    @Test
    void testGenericExceptionHandler() {
        Exception ex = new Exception("Unhandled exception");
        ResponseEntity<ApiResponse<String>> response = handler.handleAll(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(500, response.getBody().getCode());
    }
}