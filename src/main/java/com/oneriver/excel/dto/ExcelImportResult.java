package com.oneriver.excel.dto;

import java.util.List;

public record ExcelImportResult<T>(
        List<T> data,
        List<String> errors
) {}