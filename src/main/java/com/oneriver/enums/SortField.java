package com.oneriver.enums;

import com.oneriver.utils.FundConstants;
import lombok.Getter;

import java.util.Locale;
import java.util.Optional;

@Getter
public enum SortField {
    ONE_MONTH(FundConstants.SORT_ONE_MONTH, FundConstants.ES_FIELD_ONE_MONTH),
    THREE_MONTHS(FundConstants.SORT_THREE_MONTHS, FundConstants.ES_FIELD_THREE_MONTHS),
    SIX_MONTHS(FundConstants.SORT_SIX_MONTHS, FundConstants.ES_FIELD_SIX_MONTHS),
    YEAR_TO_DATE(FundConstants.SORT_YEAR_CHANGE, FundConstants.ES_FIELD_YEAR_TO_DATE),
    YTD(FundConstants.SORT_YTD, FundConstants.ES_FIELD_YEAR_TO_DATE),
    ONE_YEAR(FundConstants.SORT_ONE_YEAR, FundConstants.ES_FIELD_ONE_YEAR),
    THREE_YEARS(FundConstants.SORT_THREE_YEARS, FundConstants.ES_FIELD_THREE_YEARS),
    FIVE_YEARS(FundConstants.SORT_FIVE_YEARS, FundConstants.ES_FIELD_FIVE_YEARS),
    FUND_CODE(FundConstants.SORT_FUND_CODE, FundConstants.ES_FIELD_FUND_CODE),
    FUND_NAME(FundConstants.SORT_FUND_NAME, FundConstants.ES_FIELD_FUND_NAME_KEYWORD),
    UMBRELLA_FUND_TYPE(FundConstants.SORT_UMBRELLA_FUND_TYPE, FundConstants.ES_FIELD_UMBRELLA_FUND_TYPE);

    private final String key;
    private final String esField;

    SortField(String key, String esField) {
        this.key = key;
        this.esField = esField;
    }

    public static Optional<SortField> fromString(String s) {
        if (s == null) return Optional.empty();
        String normalized = s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        for (SortField sf : values()) {
            if (sf.key.equals(normalized)) return Optional.of(sf);
        }
        return Optional.empty();
    }
}