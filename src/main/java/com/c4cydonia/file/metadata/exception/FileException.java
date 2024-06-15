package com.c4cydonia.file.metadata.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileException extends RuntimeException {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String errorMessage;

    public FileException(HttpStatus status, String errorMessage) {
        super(errorMessage);
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.errorMessage = errorMessage;
    }

    @Override
    public Throwable fillInStackTrace() {
        // Prevent stack trace from being filled
        return this;
    }
}
