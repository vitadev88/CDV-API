package com.congdongviet.blog.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class SlugUtils {

    private static final Pattern NON_ASCII = Pattern.compile("[^\\p{ASCII}]");
    private static final Pattern NON_SLUG  = Pattern.compile("[^a-z0-9\\-]");
    private static final Pattern DASHES    = Pattern.compile("-{2,}");

    private SlugUtils() {}

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) return "";
        // Decompose Unicode (e.g. "ộ" → "o" + combining marks), then strip combining marks
        String normalized = Normalizer.normalize(input.trim().toLowerCase(), Normalizer.Form.NFD);
        String ascii = NON_ASCII.matcher(normalized).replaceAll("");
        String slug  = NON_SLUG.matcher(ascii.replace(' ', '-')).replaceAll("");
        return DASHES.matcher(slug).replaceAll("-").replaceAll("^-|-$", "");
    }
}
