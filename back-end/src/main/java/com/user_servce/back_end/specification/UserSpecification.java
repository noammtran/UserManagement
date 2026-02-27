package com.user_servce.back_end.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.user_servce.back_end.dto.UserFilterRequest;
import com.user_servce.back_end.entity.User;

import jakarta.persistence.criteria.Predicate;

public final class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> filter(UserFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                String like = "%" + filter.getKeyword().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("userName")), like),
                    cb.like(cb.lower(root.get("fullName")), like),
                    cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("phoneNumber")), like),
                    cb.like(cb.lower(root.get("identityNumber")), like)
                ));
            }

            if (filter.getStatusFlag() != null) {
                predicates.add(cb.equal(root.get("statusFlag"), filter.getStatusFlag()));
            }

            if (filter.getDepartmentId() != null) {
                predicates.add(cb.equal(root.get("department").get("id"), filter.getDepartmentId()));
            }

            if (filter.getPositionCode() != null) {
                predicates.add(cb.equal(root.get("positionCode"), filter.getPositionCode()));
            }

            if (filter.getCompanyProfileId() != null) {
                predicates.add(cb.equal(root.get("companyProfile").get("id"), filter.getCompanyProfileId()));
            }

            if (filter.getGenderCode() != null) {
                predicates.add(cb.equal(root.get("genderCode"), filter.getGenderCode()));
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
