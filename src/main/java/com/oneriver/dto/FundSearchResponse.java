package com.oneriver.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundSearchResponse {
    private String fundCode;
    private String fundName;
    private String umbrellaFundType;
    private Map<String, BigDecimal> returnPeriods;
}