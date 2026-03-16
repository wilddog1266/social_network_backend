package com.example.social_graph_service.exception;


import com.example.common.exception.BadRequestException;

public class CannotFollowYourselfException extends BadRequestException {
    public CannotFollowYourselfException() {
        super("User can't subscribe to himself");
    }
}
