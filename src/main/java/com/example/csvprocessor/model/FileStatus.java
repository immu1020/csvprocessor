package com.example.csvprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the status and location of a file being processed.
 * <p>
 * Used to track whether a file is currently being processed or has completed,
 * along with its storage path.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileStatus {

    /**
     * Current processing status of the file.
     * Expected values: "processing", "completed"
     */
    private String status;

    /**
     * Absolute or relative path to the file on disk.
     */
    private String path;
}