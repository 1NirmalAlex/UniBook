package com.unibook.controller;

import com.unibook.dao.KuppiyaDAO;
import com.unibook.model.User;
import com.unibook.util.ValidationUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/kuppiya")
public class KuppiyaServlet extends HttpServlet {

    private static final int LIST_LIMIT = 60;

    private final KuppiyaDAO kuppiyaDAO = new KuppiyaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            request.setAttribute("kuppiyaSessions", kuppiyaDAO.findUpcomingForViewer(logged.getUserId(), LIST_LIMIT));
            request.setAttribute("navActive", "kuppiya");
            request.getRequestDispatcher("/kuppiya.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String ctx = request.getContextPath();
        if (action == null) {
            response.sendRedirect(ctx + "/kuppiya");
            return;
        }

        try {
            switch (action) {
                case "create":
                    handleCreate(request, response, logged.getUserId(), ctx);
                    break;
                case "join":
                    handleJoin(request, response, logged.getUserId(), ctx);
                    break;
                case "leave":
                    handleLeave(request, response, logged.getUserId(), ctx);
                    break;
                case "delete":
                    handleDelete(request, response, logged.getUserId(), ctx);
                    break;
                default:
                    response.sendRedirect(ctx + "/kuppiya");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String subject = request.getParameter("subject");
        String description = request.getParameter("description");
        String dtStr = request.getParameter("sessionDateTime");

        if (ValidationUtil.isBlank(subject) || ValidationUtil.isBlank(dtStr)) {
            response.sendRedirect(ctx + "/kuppiya?error=fields");
            return;
        }

        LocalDateTime when;
        try {
            when = LocalDateTime.parse(dtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            response.sendRedirect(ctx + "/kuppiya?error=date");
            return;
        }

        try {
            kuppiyaDAO.createSession(userId, subject, description, when);
            response.sendRedirect(ctx + "/kuppiya?created=1");
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("Subject too long")) {
                response.sendRedirect(ctx + "/kuppiya?error=len");
            } else {
                throw e;
            }
        }
    }

    private void handleJoin(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("sessionId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/kuppiya");
            return;
        }
        try {
            long sid = Long.parseLong(idStr);
            if (!kuppiyaDAO.joinSession(sid, userId)) {
                response.sendRedirect(ctx + "/kuppiya?error=join");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/kuppiya");
            return;
        }
        response.sendRedirect(ctx + "/kuppiya?joined=1");
    }

    private void handleLeave(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("sessionId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/kuppiya");
            return;
        }
        try {
            long sid = Long.parseLong(idStr);
            int r = kuppiyaDAO.leaveSession(sid, userId);
            if (r == -1) {
                response.sendRedirect(ctx + "/kuppiya?error=host");
                return;
            }
            if (r == 0) {
                response.sendRedirect(ctx + "/kuppiya?error=leave");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/kuppiya");
            return;
        }
        response.sendRedirect(ctx + "/kuppiya?left=1");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("sessionId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/kuppiya");
            return;
        }
        try {
            long sid = Long.parseLong(idStr);
            if (!kuppiyaDAO.deleteIfHost(sid, userId)) {
                response.sendRedirect(ctx + "/kuppiya?error=del");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/kuppiya");
            return;
        }
        response.sendRedirect(ctx + "/kuppiya?deleted=1");
    }
}
