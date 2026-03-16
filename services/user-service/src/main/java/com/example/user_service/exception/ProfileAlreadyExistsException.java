package com.example.user_service.exception;

import com.example.common.exception.ConflictException;

public class ProfileAlreadyExistsException extends ConflictException {
    public ProfileAlreadyExistsException(String message) {
        super(message);
    }
}
