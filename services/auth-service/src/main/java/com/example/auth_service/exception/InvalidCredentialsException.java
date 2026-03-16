package com.example.auth_service.exception;

import com.example.common.exception.BadRequestException;

public class InvalidCredentialsException extends BadRequestException {

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
