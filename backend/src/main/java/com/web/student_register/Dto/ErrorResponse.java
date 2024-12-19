package com.web.student_register.Dto;

import java.time.LocalDateTime;
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(LocalDateTime timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }


    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
