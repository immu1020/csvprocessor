package com.example.csvprocessor.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * Scheduled component responsible for cleaning up old files from the upload directory.
 * <p>
 * This scheduler runs daily at 2 AM and deletes files older than 7 days from the configured
 * {@code uploaded-files} directory. It logs all deletions and handles errors gracefully.
 */
@Component
public class FileCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FileCleanupScheduler.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Scheduled task that runs daily at 2 AM to delete files older than 7 days.
     * <p>
     * Skips cleanup if the upload directory does not exist. Logs each deleted file
     * and any errors encountered during deletion or directory access.
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanOldFiles() {
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            logger.warn("Upload directory '{}' does not exist. Skipping cleanup.", uploadDir);
            return;
        }

        try (Stream<Path> files = Files.list(path)) {
            files.filter(Files::isRegularFile)
                    .filter(file -> isOlderThanDays(file, 7))
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                            logger.info("Deleted old file: {}", file.getFileName());
                        } catch (IOException e) {
                            logger.error("Failed to delete file '{}': {}", file.getFileName(), e.getMessage());
                        }
                    });
        } catch (IOException e) {
            logger.error("Error accessing upload directory '{}': {}", uploadDir, e.getMessage());
        }
    }

    /**
     * Checks whether a file is older than the specified number of days.
     *
     * @param file the file path to check
     * @param days the age threshold in days
     * @return {@code true} if the file is older than the threshold; {@code false} otherwise
     */
    private boolean isOlderThanDays(Path file, int days) {
        try {
            FileTime lastModified = Files.getLastModifiedTime(file);
            return lastModified.toInstant().isBefore(Instant.now().minus(days, ChronoUnit.DAYS));
        } catch (IOException e) {
            logger.error("Failed to read last modified time for '{}': {}", file.getFileName(), e.getMessage());
            return false;
        }
    }
}