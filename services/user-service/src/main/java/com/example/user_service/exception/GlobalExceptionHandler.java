package com.example.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleProfileAlreadyExistsException(ProfileAlreadyExistsException e) {
        ApiErrorResponse response = new ApiErrorResponse();

        response.setMessage(e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProfileNotFoundException(ProfileNotFoundException e) {
        ApiErrorResponse response = new ApiErrorResponse();

        response.setMessage(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
