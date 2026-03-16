package com.example.common.handler;

import com.example.common.exception.BadRequestException;
import com.example.common.exception.ConflictException;
import com.example.common.exception.NotFoundException;
import com.example.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException e, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(e.getMessage(), 404, request));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflictException(ConflictException e, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse(e.getMessage(), 409, request));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(BadRequestException e, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(e.getMessage(), 400, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        ApiErrorResponse response = buildErrorResponse("Validation failed", 400, request);

        Map<String, String> errors = new HashMap<>();

        for(FieldError err : e.getBindingResult().getFieldErrors()) {
            errors.put(err.getField(), err.getDefaultMessage());
        }

        response.setErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(e.getMessage(), 500, request));
    }

    private ApiErrorResponse buildErrorResponse(String message, int status, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse();

        response.setMessage(message);
        response.setTimestamp(Instant.now());
        response.setPath(request.getRequestURI());
        response.setStatus(status);

        return response;
    }
}
