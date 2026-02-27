package com.user_servce.back_end.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusChangeRequest {
    @NotEmpty
    private List<String> ids;

    @NotNull
    private Integer statusFlag;
}
