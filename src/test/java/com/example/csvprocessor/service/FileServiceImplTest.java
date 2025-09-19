package com.example.csvprocessor.service;

import com.example.csvprocessor.dto.UploadResponseDto;
import com.example.csvprocessor.service.impl.FileServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceImplTest {

    private FileServiceImpl fileService;

    @BeforeEach
    void setup() {
        fileService = new FileServiceImpl();
    }

    @Test
    void shouldProcessCsvAndReturnId() {
        String csvContent = "name,email\nJohn,john@example.com\nJane,jane.com\n";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        UploadResponseDto response = fileService.processFile(file);
        assertNotNull(response.getId());
    }

    @Test
    void shouldThrowExceptionForEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.processFile(file);
        });

        assertEquals("Uploaded file is empty", exception.getMessage());
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.walk(Paths.get("uploaded-files"))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
    }
}