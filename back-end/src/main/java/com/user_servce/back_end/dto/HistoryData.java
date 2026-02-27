package com.user_servce.back_end.dto;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HistoryData {
    List<UserHistoryResponse> items;
    PaginationInfo pagination;
}
