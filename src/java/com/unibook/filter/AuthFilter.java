package com.unibook.filter;

import com.unibook.model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Requires a logged-in session for all routes except a small public allow-list.
 * Keeps direct URL access to JSPs and servlets from bypassing authentication.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String ctx = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(ctx.length());
        if (path.isEmpty()) {
            path = "/";
        }

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User logged = session == null ? null : (User) session.getAttribute("loggedUser");
        if (logged == null) {
            String redirect = ctx + "/login.jsp";
            if (!"/favicon.ico".equals(path)) {
                String q = req.getQueryString();
                String full = path + (q != null && !q.isEmpty() ? "?" + q : "");
                if (!full.equals("/")) {
                    redirect = ctx + "/login.jsp?next=" + java.net.URLEncoder.encode(full, java.nio.charset.StandardCharsets.UTF_8);
                }
            }
            resp.sendRedirect(redirect);
            return;
        }

        chain.doFilter(request, response);
    }

    private static boolean isPublicPath(String path) {
        if (path.equals("/") || path.equals("/index.jsp")) {
            return true;
        }
        if (path.equals("/login.jsp") || path.equals("/signup.jsp") || path.equals("/error.jsp")
                || path.equals("/campusRegistration.jsp")) {
            return true;
        }
        if (path.startsWith("/css/") || path.startsWith("/uploads/")) {
            return true;
        }
        if (path.equals("/favicon.ico")) {
            return true;
        }
        // Auth servlets & campus demo (no session required)
        if (path.equals("/loginUser") || path.equals("/signup") || path.equals("/registerCampus")
                || path.equals("/logout")) {
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
