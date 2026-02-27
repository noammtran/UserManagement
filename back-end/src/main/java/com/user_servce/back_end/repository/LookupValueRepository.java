package com.user_servce.back_end.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_servce.back_end.entity.LookupValue;

public interface LookupValueRepository extends JpaRepository<LookupValue, Long> {

    List<LookupValue> findByLookupGroupAndStatusFlag(String lookupGroup, Integer statusFlag);

    Optional<LookupValue> findByLookupGroupAndLookupCode(String lookupGroup, Integer lookupCode);
}
