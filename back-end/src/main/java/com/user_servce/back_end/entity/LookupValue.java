package com.user_servce.back_end.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
    name = "LOOKUP_VALUES",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_LOOKUP_GROUP_CD", columnNames = {"LOOKUP_GROUP", "LOOKUP_CD"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LookupValue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lookup_values_seq")
    @SequenceGenerator(name = "lookup_values_seq", sequenceName = "SEQ_LOOKUP_VALUES", allocationSize = 1)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "LOOKUP_VALUE", nullable = false, length = 255)
    private String lookupValue;

    @Column(name = "LOOKUP_CD", nullable = false)
    private Integer lookupCode;

    @Column(name = "LOOKUP_GROUP", nullable = false, length = 20)
    private String lookupGroup;

    @Column(name = "STATUS_FLG", nullable = false)
    private Integer statusFlag;

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
