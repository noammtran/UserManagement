package com.user_servce.back_end.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeleteUsersRequest {
    @NotEmpty
    private List<String> ids;

    private Boolean hardDelete;
}
