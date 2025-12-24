package com.oneriver.enums;

import java.util.Arrays;

public enum FundExcelColumn {
    FUND_CODE("Fon Kodu"),
    FUND_NAME("Fon Adı"),
    UMBRELLA_FUND_TYPE("Şemsiye Fon Türü"),
    ONE_MONTH("1 Ay (%)"),
    THREE_MONTH("3 Ay (%)"),
    SIX_MONTH("6 Ay (%)"),
    YTD("Yılbaşı (%)"),
    ONE_YEAR("1 Yıl (%)"),
    THREE_YEAR("3 Yıl (%)"),
    FIVE_YEAR("5 Yıl (%)");

    private final String keyword;

    FundExcelColumn(String keyword) {
        this.keyword = keyword;
    }

    public static FundExcelColumn fromHeader(String header) {
        if (header == null || header.isBlank()) return null;

        return Arrays.stream(values())
                .filter(col -> header.contains(col.keyword))
                .findFirst()
                .orElse(null);
    }}
