package com.eatwhat.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eatwhat.backend.dto.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        logger.warn("Business exception occurred: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(ValidationException e) {
        logger.warn("Validation exception occurred: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid input: " + e.getMessage(), 400));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("Illegal argument exception: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException e) {
        logger.warn("Illegal state exception: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 409));
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception e) {
        logger.error("Unexpected error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later.", 500));
    }
}