package com.example.venuva.Infrastructure.Config;

import com.example.venuva.Shared.Dtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for all REST controllers.
 * Converts exceptions to clean error responses without exposing internal details.
 * Logs full details internally for debugging.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle IllegalArgumentException → 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        log.error("[REQUEST] {} {} - IllegalArgumentException: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
                "Invalid input provided",
                "INVALID_ARGUMENT"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle validation errors from @Valid annotations → 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        BindingResult bindingResult = ex.getBindingResult();
        String errors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.error("[REQUEST] {} {} - Validation Error: {}", 
                request.getMethod(), request.getRequestURI(), errors, ex);
        
        ErrorResponse error = new ErrorResponse(
                "Validation failed: " + errors,
                "VALIDATION_ERROR"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle EntityNotFoundException and NoSuchElementException → 404 Not Found
     */
    @ExceptionHandler({
            EntityNotFoundException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException ex,
            HttpServletRequest request) {
        
        log.error("[REQUEST] {} {} - Not Found: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
                "The requested resource was not found",
                "NOT_FOUND"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle DataIntegrityViolationException (e.g., duplicate email, foreign key violations) → 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        
        String cause = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        log.error("[REQUEST] {} {} - DataIntegrityViolation: {}", 
                request.getMethod(), request.getRequestURI(), cause, ex);
        
        // Determine if it's a duplicate key or other constraint violation
        String userMessage = "The operation could not be completed due to a data constraint violation";
        String code = "CONFLICT";
        
        if (cause != null && cause.toLowerCase().contains("duplicate")) {
            userMessage = "A record with this value already exists";
            code = "DUPLICATE_ENTRY";
        }
        
        ErrorResponse error = new ErrorResponse(userMessage, code);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle RuntimeException (catch-all for service-level exceptions) → 400 Bad Request
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        
        log.error("[REQUEST] {} {} - RuntimeException: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
                "An operation failed. Please try again",
                "OPERATION_FAILED"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle all other Exceptions → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("[REQUEST] {} {} - Unhandled Exception: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
                "An unexpected error occurred. Please contact support if the problem persists",
                "INTERNAL_SERVER_ERROR"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
