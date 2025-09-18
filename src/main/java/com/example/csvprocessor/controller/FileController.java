package com.example.csvprocessor.controller;

import com.example.csvprocessor.dto.ApiResponse;
import com.example.csvprocessor.dto.UploadResponseDto;
import com.example.csvprocessor.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/API")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<UploadResponseDto>> upload(@RequestParam("file") MultipartFile file) {
        UploadResponseDto response = fileService.processFile(file);
        return new ResponseEntity<>(
                new ApiResponse<>("File uploaded successfully", 200, response),
                HttpStatus.OK
        );
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        FileSystemResource fileResource = fileService.getFileById(id);

        try {
            byte[] fileBytes = Files.readAllBytes(fileResource.getFile().toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(fileBytes.length);
            headers.setContentDisposition(ContentDisposition.attachment().filename("processed.csv").build());

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
