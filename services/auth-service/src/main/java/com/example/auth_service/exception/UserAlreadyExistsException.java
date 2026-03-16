package com.example.auth_service.exception;

import com.example.common.exception.BadRequestException;

public class UserAlreadyExistsException extends BadRequestException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
