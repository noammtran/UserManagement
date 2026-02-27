package com.user_servce.back_end.exception;

import java.util.Map;

public class FieldConflictException extends RuntimeException {

    private final Map<String, String> errors;

    public FieldConflictException(Map<String, String> errors) {
        super("Duplicate fields");
        this.errors = Map.copyOf(errors);
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}

