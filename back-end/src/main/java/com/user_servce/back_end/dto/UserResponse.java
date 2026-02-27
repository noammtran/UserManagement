package com.user_servce.back_end.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id;
    private String userName;
    private String fullName;
    private String birthDate;
    private Integer genderCode;
    private String address;
    private String phoneNumber;
    private String faxNumber;
    private String email;
    private String identityNumber;
    private Integer identityTypeCode;
    private String identityIssuedDate;
    private String identityIssuedPlace;
    private Long departmentId;
    private String departmentName;
    private Integer positionCode;
    private String statusFlag;
    private Long companyProfileId;
    private String companyName;
    private String stockCode;
    private String stockExchange;
    private String positionTitle;
    private String description;
    private String createdBy;
    private String createdDate;
    private String lastUpdatedBy;
    private String lastUpdatedDate;
}
