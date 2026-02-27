package com.user_servce.back_end.dto;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MasterDataResponse {
    List<LookupValueResponse> genders;
    List<LookupValueResponse> positions;
    List<LookupValueResponse> identityTypes;
    List<LookupValueResponse> userStatuses;
    List<DepartmentBrief> departments;
    List<CompanyBrief> companies;
}
