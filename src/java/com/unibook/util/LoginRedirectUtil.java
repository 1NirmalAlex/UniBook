package com.unibook.util;

/**
 * Builds a safe in-app redirect target after login (blocks open redirects and path tricks).
 */
public final class LoginRedirectUtil {

    private LoginRedirectUtil() {
    }

    public static String safePostLoginRedirect(String next, String contextPath) {
        if (next == null || next.trim().isEmpty()) {
            return contextPath + "/feed";
        }
        String n = next.trim();
        if (n.indexOf('\r') >= 0 || n.indexOf('\n') >= 0 || n.indexOf('\0') >= 0) {
            return contextPath + "/feed";
        }
        if (n.contains("..") || n.contains("//") || n.toLowerCase().contains("javascript:")) {
            return contextPath + "/feed";
        }
        if (!n.startsWith("/")) {
            return contextPath + "/feed";
        }
        return contextPath + n;
    }
}
