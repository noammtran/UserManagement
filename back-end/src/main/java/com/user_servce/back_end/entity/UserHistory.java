package com.user_servce.back_end.entity;

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
    name = "USER_HISTORY",
    indexes = {
        @Index(name = "IDX_USER_HISTORY_USER", columnList = "USER_ID"),
        @Index(name = "IDX_USER_HISTORY_FIELD", columnList = "FIELD_NAME")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserHistory {

    @Id
    @Column(name = "ID", length = 36)
    @EqualsAndHashCode.Include
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "FIELD_NAME", nullable = false, length = 20)
    private String fieldName;

    @Column(name = "OLD_VALUE", nullable = false, length = 255)
    private String oldValue;

    @Column(name = "NEW_VALUE", nullable = false, length = 255)
    private String newValue;

    @Column(name = "CHANGED_BY", nullable = false, length = 30)
    private String changedBy;

    @Column(name = "CHANGED_DATE", nullable = false)
    private LocalDateTime changedDate;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (changedDate == null) {
            changedDate = LocalDateTime.now();
        }
    }
}
