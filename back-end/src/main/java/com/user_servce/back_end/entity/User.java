package com.user_servce.back_end.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
    name = "USERS",
    indexes = {
        @Index(name = "IDX_USERS_DEPT", columnList = "DEPARTMENT_ID"),
        @Index(name = "IDX_USERS_POSITION", columnList = "POSITION_CD"),
    @Index(name = "IDX_USERS_STATUS", columnList = "STATUS_FLG")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"department", "companyProfile"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @Column(name = "ID", length = 36)
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "USER_NAME", nullable = false, length = 30, unique = true)
    private String userName;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "FULL_NAME", nullable = false, length = 50)
    private String fullName;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "GENDER_CD")
    private Integer genderCode;

    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "PHONE_NO", length = 20)
    private String phoneNumber;

    @Column(name = "FAX_NO", length = 20)
    private String faxNumber;

    @Column(name = "EMAIL", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "IDENTITY", length = 20)
    private String identityNumber;

    @Column(name = "IDENTITY_TYPE_CD")
    private Integer identityTypeCode;

    @Column(name = "IDENTITY_ISSUED_DATE")
    private LocalDate identityIssuedDate;

    @Column(name = "IDENTITY_ISSUED_PLACE", length = 20)
    private String identityIssuedPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID", nullable = false)
    private Department department;

    @Column(name = "POSITION_CD", nullable = false)
    private Integer positionCode;

    @Column(name = "STATUS_FLG", nullable = false)
    private Integer statusFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_PROFILE_ID", nullable = false)
    private CompanyProfile companyProfile;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "CREATED_BY", nullable = false, length = 30)
    private String createdBy;

    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 30)
    private String lastUpdatedBy;

    @Column(name = "LAST_UPDATED_DATE", nullable = false)
    private LocalDateTime lastUpdatedDate;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (statusFlag == null) {
            statusFlag = 1;
        }
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (lastUpdatedDate == null) {
            lastUpdatedDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdatedDate = LocalDateTime.now();
    }
}
