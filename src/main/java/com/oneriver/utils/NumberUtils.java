package com.oneriver.utils;


import java.math.BigDecimal;

public class NumberUtils {

    public static BigDecimal parsePercentage(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String cleanValue = value
                .replace("%", "")
                .replace("\u00A0", "")
                .trim();

        if (cleanValue.isEmpty() || cleanValue.equals("-")) {
            return null;
        }

        cleanValue = cleanValue.replace(",", ".");

        try {
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}