package com.user_servce.back_end.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LookupValueResponse {
    private Long id;
    private String lookupGroup;
    private Integer lookupCode;
    private String lookupValue;
    private Integer statusFlag;
    private String description;
}
