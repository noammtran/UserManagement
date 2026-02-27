package com.user_servce.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBrief {
    private Long id;
    private String companyName;
    private String stockCode;
    private String stockExchange;
}
