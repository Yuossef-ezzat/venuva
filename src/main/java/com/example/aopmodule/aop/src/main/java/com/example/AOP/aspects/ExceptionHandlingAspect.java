package com.example.aopmodule.aop.src.main.java.com.example.AOP.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.aopmodule.aop.src.main.java.com.example.AOP.Annotation.HandleException;

import java.util.HashMap;
import java.util.Map;


@Component
@Aspect
public class ExceptionHandlingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingAspect.class);

 
    @Around("@annotation(handleEx)")
    public Object handleException(ProceedingJoinPoint joinPoint, HandleException handleEx) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input: {}", e.getMessage());
            return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input", e);
        } catch (NullPointerException e) {
            logger.error("Null pointer error: {}", e.getMessage());
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal system error", e);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(
            HttpStatus status, String message, Exception exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("message", message);
        errorResponse.put("error", exception.getClass().getSimpleName());
        errorResponse.put("timestamp", System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, status);
    }
}