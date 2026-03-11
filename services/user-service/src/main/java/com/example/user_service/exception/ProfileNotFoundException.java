package com.example.user_service.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException() {
        super("Profile not found");
    }
}
