package com.user_servce.back_end.dto;

import lombok.Data;

@Data
public class UserFilterRequest {
    private String keyword;
    private Integer statusFlag;
    private Long departmentId;
    private Integer positionCode;
    private Long companyProfileId;
    private Integer genderCode;
    private int page = 0;
    private int size = 10;
}
