package com.example.csvprocessor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper used to standardize HTTP responses.
 *
 * @param <T> the type of data included in the response
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResponse<T> {

    /**
     * Human-readable message describing the response.
     */
    private String message;

    /**
     * HTTP status code associated with the response.
     */
    @JsonProperty("status")
    private int status;

    /**
     * The actual response payload of type T.
     */
    private T data;

    @JsonProperty("code")
    public int getCode() {
        return status;
    }

}
