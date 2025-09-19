package com.example.csvprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the response returned after a successful file upload.
 * <p>
 * Contains the unique identifier assigned to the uploaded file.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadResponseDto {

    /**
     * Unique identifier for the uploaded file.
     */
    private String id;
}
