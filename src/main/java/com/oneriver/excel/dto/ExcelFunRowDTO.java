package com.oneriver.excel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFunRowDTO {
    private String fundCode;
    private String fundName;
    private String umbrellaFundType;
    private String oneMonth;
    private String threeMonth;
    private String sixMonth;
    private String yearToDate;
    private String oneYear;
    private String threeYear;
    private String fiveYear;
}
