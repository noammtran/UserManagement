package com.user_servce.back_end.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_servce.back_end.dto.CompanyBrief;
import com.user_servce.back_end.dto.DepartmentBrief;
import com.user_servce.back_end.dto.LookupValueResponse;
import com.user_servce.back_end.dto.MasterDataResponse;
import com.user_servce.back_end.entity.CompanyProfile;
import com.user_servce.back_end.entity.Department;
import com.user_servce.back_end.entity.LookupValue;
import com.user_servce.back_end.repository.CompanyProfileRepository;
import com.user_servce.back_end.repository.DepartmentRepository;
import com.user_servce.back_end.repository.LookupValueRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MasterDataService {

    private final LookupValueRepository lookupValueRepository;
    private final DepartmentRepository departmentRepository;
    private final CompanyProfileRepository companyProfileRepository;

    public MasterDataResponse getMasterData() {
        List<LookupValueResponse> genders = mapLookup(lookupValueRepository.findByLookupGroupAndStatusFlag("GENDER", 1));
        List<LookupValueResponse> positions = mapLookup(lookupValueRepository.findByLookupGroupAndStatusFlag("POSITION", 1));
        List<LookupValueResponse> identityTypes = mapLookup(lookupValueRepository.findByLookupGroupAndStatusFlag("ID_TYPE", 1));
        List<LookupValueResponse> userStatuses = mapLookup(lookupValueRepository.findByLookupGroupAndStatusFlag("USER_STATUS", 1));

        List<DepartmentBrief> departments = departmentRepository.findAll().stream()
            .map(this::mapDepartment)
            .collect(Collectors.toList());

        List<CompanyBrief> companies = companyProfileRepository.findAll().stream()
            .map(this::mapCompany)
            .collect(Collectors.toList());

        return MasterDataResponse.builder()
            .genders(genders)
            .positions(positions)
            .identityTypes(identityTypes)
            .userStatuses(userStatuses)
            .departments(departments)
            .companies(companies)
            .build();
    }

    private List<LookupValueResponse> mapLookup(List<LookupValue> values) {
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

    private DepartmentBrief mapDepartment(Department dept) {
        return new DepartmentBrief(dept.getId(), dept.getDepartmentName());
    }

    private CompanyBrief mapCompany(CompanyProfile company) {
        return new CompanyBrief(company.getId(), company.getCompanyName(), company.getStockCode(), company.getStockExchange());
    }
}
