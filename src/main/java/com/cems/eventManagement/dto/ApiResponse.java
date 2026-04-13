package com.cems.eventManagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(){

    }

    // Constructor for Success Responses (With Data)
    public ApiResponse(boolean success, String message, T data){
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for Error Responses (Without Data)
    public ApiResponse(boolean success, String message){
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
