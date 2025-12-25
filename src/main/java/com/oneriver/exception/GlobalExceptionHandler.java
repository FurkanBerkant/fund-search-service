package com.oneriver.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(
            IOException ex, WebRequest request) {

        log.error("IO Exception occurred", ex);

        Map<String, Object> response = buildErrorResponse(
                "FILE_PROCESSING_ERROR",
                "Failed to process file: " + ex.getMessage(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        log.warn("Validation constraint violation: {}", ex.getMessage());

        String errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        Map<String, Object> response = buildErrorResponse(
                "VALIDATION_ERROR",
                errors,
                request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Method argument not valid: {}", ex.getMessage());

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        Map<String, Object> response = buildErrorResponse(
                "VALIDATION_ERROR",
                errors,
                request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSizeException(
            MaxUploadSizeExceededException ex, WebRequest request) {

        log.warn("File size exceeded: {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                "FILE_SIZE_EXCEEDED",
                "File size exceeds maximum allowed size",
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        log.error("Runtime exception occurred", ex);

        Map<String, Object> response = buildErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Unexpected exception occurred", ex);

        Map<String, Object> response = buildErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later.",
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UncategorizedElasticsearchException.class)
    public ResponseEntity<Map<String, Object>> handleElasticsearchException(
            Exception ex, WebRequest request) {

        log.error("Elasticsearch error occurred", ex);

        Map<String, Object> response = buildErrorResponse(
                "SEARCH_ERROR",
                "Search service temporarily unavailable",
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    private Map<String, Object> buildErrorResponse(
            String error, String message, String path) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", path.replace("uri=", ""));

        return response;
    }

    @ExceptionHandler(NoSuchIndexException.class)
    public ResponseEntity<Map<String,Object>> handleNoIndex(Exception ex, WebRequest req) {
        Map<String,Object> resp = buildErrorResponse("INDEX_NOT_FOUND","Index not found: " + ex.getMessage(), req.getDescription(false));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

}