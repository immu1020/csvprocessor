package com.example.csvprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileStatus {
    private String status; // "processing", "completed"
    private String path;
}
