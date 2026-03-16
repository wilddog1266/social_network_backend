package com.example.common.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
public class ApiErrorResponse {

    private Instant timestamp;

    private int status;

    private String path;

    private String message;

    private Map<String, String> errors;
}
