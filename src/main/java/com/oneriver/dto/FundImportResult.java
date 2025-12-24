package com.oneriver.dto;

import java.util.List;

public record FundImportResult(
        int total,
        int success,
        int failed,
        List<String> savedFundCodes,
        List<String> errors
) {}