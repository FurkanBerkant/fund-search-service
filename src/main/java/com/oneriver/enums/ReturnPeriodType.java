package com.oneriver.enums;

import com.oneriver.utils.FundConstants;
import lombok.Getter;

import java.util.Locale;
import java.util.Optional;

public enum ReturnPeriodType {
    ONE_MONTH(new String[]{"onemonth", "1month", "1_month", "1m"}, FundConstants.ES_FIELD_ONE_MONTH),
    THREE_MONTHS(new String[]{"threemonths", "3months", "3_months", "3m"}, FundConstants.ES_FIELD_THREE_MONTHS),
    SIX_MONTHS(new String[]{"sixmonths", "6months", "6_months", "6m"}, FundConstants.ES_FIELD_SIX_MONTHS),
    YEAR_TO_DATE(new String[]{"yeartodate", "ytd", "year_to_date"}, FundConstants.ES_FIELD_YEAR_TO_DATE),
    ONE_YEAR(new String[]{"oneyear", "1year", "1_year", "1y"}, FundConstants.ES_FIELD_ONE_YEAR),
    THREE_YEARS(new String[]{"threeyears", "3years", "3_years", "3y"}, FundConstants.ES_FIELD_THREE_YEARS),
    FIVE_YEARS(new String[]{"fiveyears", "5years", "5_years", "5y"}, FundConstants.ES_FIELD_FIVE_YEARS);

    private final String[] aliases;
    @Getter
    private final String esField;

    ReturnPeriodType(String[] aliases, String esField) {
        this.aliases = aliases;
        this.esField = esField;
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    public static Optional<ReturnPeriodType> fromString(String s) {
        if (s == null) return Optional.empty();
        String normalized = normalize(s);
        for (ReturnPeriodType t : values()) {
            for (String a : t.aliases) {
                if (normalized.equals(normalize(a))) return Optional.of(t);
            }
        }
        return Optional.empty();
    }
}