package com.unibook.util;

public final class ValidationUtil {

    private ValidationUtil() {
    }

    /**
     * Campus email must end with {@code edu.lk} (case-insensitive) and contain {@code @}.
     */
    public static boolean isValidCampusEmail(String email) {
        if (email == null) {
            return false;
        }
        String e = email.trim().toLowerCase();
        return !e.isEmpty() && e.contains("@") && e.endsWith("edu.lk");
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
