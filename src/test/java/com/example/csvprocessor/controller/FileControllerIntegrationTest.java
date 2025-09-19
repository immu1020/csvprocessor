package com.example.csvprocessor.controller;

import com.example.csvprocessor.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
public class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    void uploadValidCsvShouldReturn200() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv",
                "name,email\nJohn,john@example.com".getBytes());

        when(fileService.processFile(any())).thenReturn(new com.example.csvprocessor.dto.UploadResponseDto("abc-123"));

        mockMvc.perform(multipart("/API/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("abc-123"));
    }

    @Test
    void uploadEmptyFileShouldReturn400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        doThrow(new IllegalArgumentException("Uploaded file is empty")).when(fileService).processFile(any());

        mockMvc.perform(multipart("/API/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Uploaded file is empty"));
    }

    @Test
    void downloadInvalidIdShouldReturn400() throws Exception {
        doThrow(new com.example.csvprocessor.exception.ResourceNotFoundException("Invalid file ID"))
                .when(fileService).getFileById("bad-id");

        mockMvc.perform(get("/API/download/bad-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid file ID"));
    }

    @Test
    void downloadInProgressShouldReturn423() throws Exception {
        doThrow(new com.example.csvprocessor.exception.ProcessingInProgressException("File processing not completed yet"))
                .when(fileService).getFileById("pending-id");

        mockMvc.perform(get("/API/download/pending-id"))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.error").value("File processing not completed yet"));
    }
}