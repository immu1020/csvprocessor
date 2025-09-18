package com.example.csvprocessor.service;

import com.example.csvprocessor.dto.UploadResponseDto;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    UploadResponseDto processFile(MultipartFile file);
    FileSystemResource getFileById(String id);
}
