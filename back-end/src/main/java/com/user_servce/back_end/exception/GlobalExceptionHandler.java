package com.user_servce.back_end.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.user_servce.back_end.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FieldConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleFieldConflict(FieldConflictException ex) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
            .success(false)
            .statusCode(HttpStatus.CONFLICT.value())
            .message("Duplicate fields")
            .errors(ex.getErrors())
            .data(null)
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiResponse<Void> body = ApiResponse.<Void>builder()
            .success(false)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .message("Validation failed")
            .errors(errors.isEmpty() ? null : errors)
            .data(null)
            .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        ApiResponse<Void> body = ApiResponse.<Void>builder()
            .success(false)
            .statusCode(status)
            .message(ex.getReason() != null ? ex.getReason() : "Request failed")
            .data(null)
            .build();
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, String> fieldErrors = mapUniqueConstraintToFieldErrors(ex);
        ApiResponse<Void> body = ApiResponse.<Void>builder()
            .success(false)
            .statusCode(HttpStatus.CONFLICT.value())
            .message(fieldErrors != null ? "Duplicate fields" : "Data integrity violation")
            .errors(fieldErrors)
            .data(null)
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    private static Map<String, String> mapUniqueConstraintToFieldErrors(DataIntegrityViolationException ex) {
        String constraintName = extractConstraintName(ex);
        if (constraintName == null) {
            return null;
        }
        return switch (constraintName.toUpperCase()) {
            case "UQ_USERS_USERNAME" -> Map.of("userName", "Username already exists");
            case "UQ_USERS_EMAIL" -> Map.of("email", "Email already exists");
            case "UQ_USERS_PHONE" -> Map.of("phoneNumber", "Phone number already exists");
            default -> null;
        };
    }

    private static String extractConstraintName(DataIntegrityViolationException ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof org.hibernate.exception.ConstraintViolationException cve) {
                return cve.getConstraintName();
            }
            current = current.getCause();
        }

        Throwable mostSpecificCause = ex.getMostSpecificCause();
        String message = mostSpecificCause != null ? mostSpecificCause.getMessage() : null;
        if (message == null) {
            return null;
        }

        Matcher matcher = Pattern.compile("\\((?:[^.]+\\.)?([A-Z0-9_]+)\\)").matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

