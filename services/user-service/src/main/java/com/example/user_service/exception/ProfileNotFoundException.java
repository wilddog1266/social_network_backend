package com.example.user_service.exception;

import com.example.common.exception.NotFoundException;

public class ProfileNotFoundException extends NotFoundException {
    public ProfileNotFoundException() {
        super("Profile not found");
    }
}
