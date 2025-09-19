package com.example.csvprocessor.controller;

import com.example.csvprocessor.dto.ApiResponse;
import com.example.csvprocessor.dto.UploadResponseDto;
import com.example.csvprocessor.exception.ResourceNotFoundException;
import com.example.csvprocessor.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * REST controller for handling file upload and download operations.
 * <p>
 * Provides endpoints to upload CSV files and retrieve processed files by ID.
 */
@RestController
@RequestMapping("/API")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * Uploads a CSV file and returns a unique file ID.
     *
     * @param file the uploaded CSV file
     * @return ResponseEntity containing the file ID on success,
     *         or an error message on failure
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            UploadResponseDto response = fileService.processFile(file);
            return ResponseEntity.ok(Map.of("id", response.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error occurred"));
        }
    }

    /**
     * Downloads a processed CSV file by its unique ID.
     *
     * @param id the unique identifier of the file
     * @return ResponseEntity containing the file bytes and headers on success,
     *         or an error message on failure
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<?> download(@PathVariable String id) {
        try {
            FileSystemResource fileResource = fileService.getFileById(id);
            byte[] fileBytes = Files.readAllBytes(fileResource.getFile().toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(fileBytes.length);
            headers.setContentDisposition(ContentDisposition.attachment().filename("processed.csv").build());

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "File read error"));
        }
    }
}
