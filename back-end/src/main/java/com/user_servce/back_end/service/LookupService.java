package com.user_servce.back_end.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_servce.back_end.dto.LookupValueResponse;
import com.user_servce.back_end.entity.LookupValue;
import com.user_servce.back_end.repository.LookupValueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LookupService {

    private final LookupValueRepository lookupValueRepository;

    public List<LookupValueResponse> getByGroup(String lookupGroup, Integer statusFlag) {
        List<LookupValue> values = lookupValueRepository.findByLookupGroupAndStatusFlag(lookupGroup, statusFlag);
        return values.stream()
            .map(v -> LookupValueResponse.builder()
                .id(v.getId())
                .lookupGroup(v.getLookupGroup())
                .lookupCode(v.getLookupCode())
                .lookupValue(v.getLookupValue())
                .statusFlag(v.getStatusFlag())
                .description(v.getDescription())
                .build())
            .collect(Collectors.toList());
    }
}
