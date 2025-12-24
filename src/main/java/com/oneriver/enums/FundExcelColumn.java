package com.oneriver.enums;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

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

    private static String normalize(String s) {
        if (s == null) return "";
        String n = s.toLowerCase(Locale.ROOT).trim();
        n = Normalizer.normalize(n, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        n = n.replaceAll("[^a-z0-9\\s]", " ");
        n = n.replaceAll("\\s+", " ");
        return n;
    }

    public static FundExcelColumn fromHeader(String header) {
        if (header == null || header.isBlank()) return null;

        String nh = normalize(header);

        return Arrays.stream(values())
                .filter(col -> normalize(col.keyword).contains(nh) || nh.contains(normalize(col.keyword)))
                .findFirst()
                .orElse(null);
    }
}
