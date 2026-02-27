package com.user_servce.back_end.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank
    @Size(max = 50)
    private String fullName;

    private LocalDate birthDate;

    private Integer genderCode;

    @Size(max = 255)
    private String address;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 20)
    private String faxNumber;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @Size(max = 20)
    private String identityNumber;

    private Integer identityTypeCode;

    private LocalDate identityIssuedDate;

    @Size(max = 20)
    private String identityIssuedPlace;

    @NotNull
    private Long departmentId;

    @NotNull
    private Integer positionCode;

    private Integer statusFlag;

    @NotNull
    private Long companyProfileId;

    @Size(max = 255)
    private String description;

    @Size(max = 18)
    private String password; // optional change password

    @Size(max = 30)
    private String lastUpdatedBy; // optional, default "system" if not provided
}
