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

@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private static final Map<String, FileStatus> fileStore = new ConcurrentHashMap<>();
    private static final String STORAGE_DIR = "uploaded-files/";

    public FileServiceImpl() {
        try {
            Files.createDirectories(Paths.get(STORAGE_DIR));
        } catch (IOException e) {
            logger.error("Could not create storage directory", e);
        }
    }

    @Override
    public UploadResponseDto processFile(MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new InvalidFileException("Uploaded file is empty or not a CSV");
        }

        String id = UUID.randomUUID().toString();
        String outputPath = STORAGE_DIR + id + ".csv";

        fileStore.put(id, new FileStatus("processing", outputPath));

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

    private void processAndSave(MultipartFile file, String outputPath) throws IOException {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))
        ) {
            String header = reader.readLine();
            if (header == null || header.trim().isEmpty()) {
                throw new InvalidFileException("CSV file missing header");
            }
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
