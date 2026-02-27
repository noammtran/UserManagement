package com.user_servce.back_end.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaginationInfo {
    int page;
    int pageSize;
    int totalItems;
    int totalPages;
    boolean hasNext;
    boolean hasPrev;
}
