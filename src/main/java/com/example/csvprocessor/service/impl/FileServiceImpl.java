package com.example.csvprocessor.service.impl;

import com.example.csvprocessor.dto.UploadResponseDto;
import com.example.csvprocessor.exception.InvalidFileException;
import com.example.csvprocessor.exception.ProcessingInProgressException;
import com.example.csvprocessor.exception.ResourceNotFoundException;
import com.example.csvprocessor.model.FileStatus;
import com.example.csvprocessor.service.FileService;
import com.example.csvprocessor.util.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the {@link FileService} interface.
 * <p>
 * Handles file upload, asynchronous processing, and retrieval of processed CSV files.
 * Adds a "flag" column to each row indicating whether any field contains a valid email.
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * In-memory store to track file processing status and location.
     */
    private static final Map<String, FileStatus> fileStore = new ConcurrentHashMap<>();

    /**
     * Directory where uploaded and processed files are stored.
     */
    private static final String STORAGE_DIR = "uploaded-files/";

    /**
     * Initializes the storage directory on service startup.
     */
    public FileServiceImpl() {
        try {
            Files.createDirectories(Paths.get(STORAGE_DIR));
        } catch (IOException e) {
            logger.error("Could not create storage directory", e);
        }
    }

    /**
     * Validates and stores the uploaded CSV file, then processes it asynchronously.
     *
     * @param file the uploaded CSV file
     * @return UploadResponseDto containing the generated file ID
     * @throws InvalidFileException if the file is empty or not a CSV
     */
    @Override
    public UploadResponseDto processFile(MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new InvalidFileException("Uploaded file is empty or not a CSV");
        }

        String id = UUID.randomUUID().toString();
        String outputPath = STORAGE_DIR + id + ".csv";

        // Mark file as "processing" in memory
        fileStore.put(id, new FileStatus("processing", outputPath));

        // Start asynchronous processing
        try {
            new Thread(() -> {
                try {
                    processAndSave(file, outputPath);
                    fileStore.get(id).setStatus("completed");
                    logger.info("File processing completed for ID: {}", id);
                } catch (IOException e) {
                    logger.error("Error processing file for ID: {}", id, e);
                    fileStore.remove(id);
                }
            }).start();
        } catch (Exception e) {
            logger.error("Error starting thread for file processing", e);
            throw new RuntimeException("Error processing file");
        }

        return new UploadResponseDto(id);
    }

    /**
     * Reads the uploaded CSV file, adds a "flag" column indicating presence of valid email,
     * and writes the result to disk.
     *
     * @param file       the uploaded file
     * @param outputPath the path to save the processed file
     * @throws IOException if reading or writing fails
     */
    private void processAndSave(MultipartFile file, String outputPath) throws IOException {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))
        ) {
            String header = reader.readLine();
            if (header == null || header.trim().isEmpty()) {
                throw new InvalidFileException("CSV file missing header");
            }

            // Write header with additional "flag" column
            writer.write(header + ",flag");
            writer.newLine();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] fields = line.split(",");
                boolean hasEmail = Arrays.stream(fields).anyMatch(EmailValidator::isValidEmail);

                writer.write(line + "," + hasEmail);
                writer.newLine();
            }
        }
    }

    /**
     * Retrieves the processed file by its ID.
     *
     * @param id the unique file identifier
     * @return FileSystemResource pointing to the processed file
     * @throws ResourceNotFoundException if the file ID is invalid or file is missing
     * @throws ProcessingInProgressException if the file is still being processed
     */
    @Override
    public FileSystemResource getFileById(String id) {
        FileStatus status = fileStore.get(id);
        if (status == null) {
            throw new ResourceNotFoundException("Invalid file ID");
        }
        if (!"completed".equals(status.getStatus())) {
            throw new ProcessingInProgressException("File processing not completed yet");
        }

        File file = new File(status.getPath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Processed file not found");
        }

        return new FileSystemResource(file);
    }
}