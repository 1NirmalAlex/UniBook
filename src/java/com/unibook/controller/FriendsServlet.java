package com.unibook.controller;

import com.unibook.dao.FriendDAO;
import com.unibook.model.FriendRelation;
import com.unibook.model.User;
import com.unibook.util.ValidationUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/friends")
public class FriendsServlet extends HttpServlet {

    private final FriendDAO friendDAO = new FriendDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        long uid = logged.getUserId();

        String q = request.getParameter("q");
        try {
            List<User> searchResults = new ArrayList<>();
            if (q != null && !q.trim().isEmpty()) {
                searchResults = friendDAO.searchUsers(uid, q.trim());
            }

            Map<Long, FriendRelation> searchRelations = new HashMap<>();
            for (User u : searchResults) {
                searchRelations.put(u.getUserId(), friendDAO.getRelation(uid, u.getUserId()));
            }

            List<User> suggestions = friendDAO.suggestUsers(uid, 12);

            Map<Long, FriendRelation> suggestRelations = new HashMap<>();
            for (User u : suggestions) {
                suggestRelations.put(u.getUserId(), friendDAO.getRelation(uid, u.getUserId()));
            }

            request.setAttribute("searchQuery", q != null ? q : "");
            request.setAttribute("searchResults", searchResults);
            request.setAttribute("searchRelations", searchRelations);
            request.setAttribute("suggestions", suggestions);
            request.setAttribute("suggestRelations", suggestRelations);
            request.setAttribute("incomingRequests", friendDAO.listIncomingPending(uid));
            request.setAttribute("outgoingRequests", friendDAO.listOutgoingPending(uid));
            request.setAttribute("navActive", "friends");
            request.getRequestDispatcher("/findFriends.jsp").forward(request, response);
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
        long uid = logged.getUserId();
        String action = request.getParameter("action");
        String ctx = request.getContextPath();

        if (action == null) {
            response.sendRedirect(ctx + "/friends");
            return;
        }

        try {
            switch (action) {
                case "send":
                    handleSend(request, response, uid, ctx);
                    break;
                case "accept":
                    handleAccept(request, response, uid, ctx);
                    break;
                case "reject":
                    handleReject(request, response, uid, ctx);
                    break;
                case "cancel":
                    handleCancel(request, response, uid, ctx);
                    break;
                default:
                    response.sendRedirect(ctx + "/friends");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void handleSend(HttpServletRequest request, HttpServletResponse response, long uid, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("targetUserId");
        String q = request.getParameter("q");
        if (idStr == null) {
            response.sendRedirect(ctx + "/friends");
            return;
        }
        try {
            long target = Long.parseLong(idStr);
            if (!friendDAO.sendRequest(uid, target)) {
                response.sendRedirect(ctx + "/friends?q=" + enc(q) + "&error=send");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/friends");
            return;
        }
        response.sendRedirect(ctx + "/friends?q=" + enc(q) + "&sent=1");
    }

    private void handleAccept(HttpServletRequest request, HttpServletResponse response, long uid, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("requestId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/friends");
            return;
        }
        try {
            long rid = Long.parseLong(idStr);
            if (!friendDAO.acceptRequest(rid, uid)) {
                response.sendRedirect(ctx + "/friends?error=accept");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/friends");
            return;
        }
        response.sendRedirect(ctx + "/friends?accepted=1");
    }

    private void handleReject(HttpServletRequest request, HttpServletResponse response, long uid, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("requestId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/friends");
            return;
        }
        try {
            long rid = Long.parseLong(idStr);
            friendDAO.rejectRequest(rid, uid);
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/friends");
            return;
        }
        response.sendRedirect(ctx + "/friends");
    }

    private void handleCancel(HttpServletRequest request, HttpServletResponse response, long uid, String ctx)
            throws SQLException, IOException {
        String reqId = request.getParameter("requestId");
        if (!ValidationUtil.isBlank(reqId)) {
            try {
                friendDAO.cancelOutgoingByRequestId(Long.parseLong(reqId), uid);
            } catch (NumberFormatException ignored) {
            }
            response.sendRedirect(ctx + "/friends");
            return;
        }
        String target = request.getParameter("targetUserId");
        if (!ValidationUtil.isBlank(target)) {
            try {
                friendDAO.cancelOutgoing(uid, Long.parseLong(target));
            } catch (NumberFormatException ignored) {
            }
        }
        response.sendRedirect(ctx + "/friends");
    }

    private static String enc(String q) {
        if (q == null || q.isEmpty()) {
            return "";
        }
        try {
            return java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return "";
        }
    }
}
