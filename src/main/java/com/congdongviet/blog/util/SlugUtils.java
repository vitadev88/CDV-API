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
        // Replace 'đ'/'Đ' manually — NFD cannot decompose these (U+0110/U+0111)
        String dediacritized = input.trim().toLowerCase().replace('đ', 'd');
        // Decompose Unicode (e.g. "ộ" → "o" + combining marks), then strip combining marks
        String normalized = Normalizer.normalize(dediacritized, Normalizer.Form.NFD);
        String ascii = NON_ASCII.matcher(normalized).replaceAll("");
        String slug  = NON_SLUG.matcher(ascii.replace(' ', '-')).replaceAll("");
        return DASHES.matcher(slug).replaceAll("-").replaceAll("^-|-$", "");
    }
}
