package com.example.venuva.Infrastructure.Config;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Shared.Dtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for converting Result<T> objects to proper ResponseEntity with correct HTTP status codes.
 * Examines error messages to determine the appropriate HTTP status code.
 */
public class ResponseUtility {

    /**
     * Convert a Result<T> to ResponseEntity with proper HTTP status codes.
     * 
     * Rules:
     * - If success: return 200 OK with the value
     * - If failure with "not found": return 404
     * - If failure with "already" or "duplicate": return 409
     * - If failure with "unauthorized" or "invalid": return 401 or 400
     * - Default failure: return 400
     * 
     * @param result The Result<T> from service layer
     * @return ResponseEntity with appropriate status and error response
     */
    public static <T> ResponseEntity<?> toResponse(Result<T> result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        }

        String message = result.getError().getMessage();
        String lowerMessage = message.toLowerCase();

        // 404 Not Found
        if (lowerMessage.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(message, "NOT_FOUND"));
        }

        // 409 Conflict (duplicate, already registered, etc.)
        if (lowerMessage.contains("already") || lowerMessage.contains("duplicate")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(message, "CONFLICT"));
        }

        // 401 Unauthorized
        if (lowerMessage.contains("unauthorized")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(message, "UNAUTHORIZED"));
        }

        // 400 Bad Request (invalid, missing, invalid request body, etc.)
        if (lowerMessage.contains("invalid") || lowerMessage.contains("missing") 
                || lowerMessage.contains("request")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(message, "BAD_REQUEST"));
        }

        // Default: 400 Bad Request
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message, "BAD_REQUEST"));
    }

    /**
     * Convert a Result<T> to ResponseEntity with a specific success status code.
     * Useful for POST operations that should return 201 CREATED instead of 200 OK.
     * 
     * @param result The Result<T> from service layer
     * @param successStatus The HTTP status to use on success (e.g., HttpStatus.CREATED)
     * @return ResponseEntity with appropriate status and response body
     */
    public static <T> ResponseEntity<?> toResponse(Result<T> result, HttpStatus successStatus) {
        if (result.isSuccess()) {
            return ResponseEntity.status(successStatus).body(result.getValue());
        }
        return toResponse(result);
    }
}
