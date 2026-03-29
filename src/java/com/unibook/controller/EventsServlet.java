package com.unibook.controller;

import com.unibook.dao.EventDAO;
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

@WebServlet("/events")
public class EventsServlet extends HttpServlet {

    private static final int LIST_LIMIT = 60;

    private final EventDAO eventDAO = new EventDAO();

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
            request.setAttribute("upcomingEvents", eventDAO.findUpcomingForViewer(logged.getUserId(), LIST_LIMIT));
            request.setAttribute("navActive", "events");
            request.getRequestDispatcher("/events.jsp").forward(request, response);
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
            response.sendRedirect(ctx + "/events");
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
                    response.sendRedirect(ctx + "/events");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String dtStr = request.getParameter("eventDateTime");
        String location = request.getParameter("location");

        if (ValidationUtil.isBlank(title) || ValidationUtil.isBlank(dtStr)) {
            response.sendRedirect(ctx + "/events?error=fields");
            return;
        }

        LocalDateTime when;
        try {
            when = LocalDateTime.parse(dtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            response.sendRedirect(ctx + "/events?error=date");
            return;
        }

        try {
            eventDAO.createEvent(userId, title, description, when, location);
            response.sendRedirect(ctx + "/events?created=1");
        } catch (SQLException e) {
            String m = e.getMessage();
            if (m != null && (m.contains("Title too long") || m.contains("Location too long"))) {
                response.sendRedirect(ctx + "/events?error=len");
            } else {
                throw e;
            }
        }
    }

    private void handleJoin(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("eventId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/events");
            return;
        }
        try {
            long eid = Long.parseLong(idStr);
            if (!eventDAO.joinEvent(eid, userId)) {
                response.sendRedirect(ctx + "/events?error=join");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/events");
            return;
        }
        response.sendRedirect(ctx + "/events?joined=1");
    }

    private void handleLeave(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("eventId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/events");
            return;
        }
        try {
            long eid = Long.parseLong(idStr);
            int r = eventDAO.leaveEvent(eid, userId);
            if (r == -1) {
                response.sendRedirect(ctx + "/events?error=host");
                return;
            }
            if (r == 0) {
                response.sendRedirect(ctx + "/events?error=leave");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/events");
            return;
        }
        response.sendRedirect(ctx + "/events?left=1");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("eventId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/events");
            return;
        }
        try {
            long eid = Long.parseLong(idStr);
            if (!eventDAO.deleteIfCreator(eid, userId)) {
                response.sendRedirect(ctx + "/events?error=del");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/events");
            return;
        }
        response.sendRedirect(ctx + "/events?deleted=1");
    }
}
