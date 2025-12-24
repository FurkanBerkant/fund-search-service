package com.oneriver.excel.dto;

import lombok.Builder;

@Builder
public record FundImportResponse(
        int total,
        int success,
        int failed
) {}