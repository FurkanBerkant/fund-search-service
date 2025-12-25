package com.oneriver.utils;

import java.text.Normalizer;
import java.util.Locale;

public final class StringUtils {

    private StringUtils() { throw new UnsupportedOperationException("Utility class"); }

    public static String normalizeForComparison(String s) {
        if (s == null) return "";
        String n = s.toLowerCase(Locale.ROOT).trim();
        n = Normalizer.normalize(n, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        n = n.replaceAll("[^a-z0-9\\s]", " ");
        n = n.replaceAll("\\s+", " ");
        return n;
    }
}