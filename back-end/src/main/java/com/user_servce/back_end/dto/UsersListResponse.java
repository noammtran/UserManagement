package com.user_servce.back_end.dto;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UsersListResponse {
    long total;
    List<UserResponse> items;
}
