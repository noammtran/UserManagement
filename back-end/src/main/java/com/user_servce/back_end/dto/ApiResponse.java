package com.user_servce.back_end.dto;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Value;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Value
@Builder
public class ApiResponse<T> {
    boolean success;
    int statusCode;
    String message;
    T data;
    @Builder.Default
    Map<String, String> errors = null;
    @Builder.Default
    Instant timestamp = Instant.now();
}
