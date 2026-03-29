package com.unibook.controller;

import com.unibook.dao.PostDAO;
import com.unibook.model.User;
import com.unibook.util.ValidationUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/feed")
public class FeedServlet extends HttpServlet {

    private static final int TIMELINE_LIMIT = 50;

    private final PostDAO postDAO = new PostDAO();

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
            request.setAttribute("timelinePosts", postDAO.findTimelineForUser(logged.getUserId(), TIMELINE_LIMIT));
            request.setAttribute("navActive", "feed");
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
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
        if ("delete".equals(action)) {
            handleDeletePost(request, response, logged.getUserId());
            return;
        }

        String content = request.getParameter("content");
        if (ValidationUtil.isBlank(content)) {
            response.sendRedirect(request.getContextPath() + "/feed?error=empty");
            return;
        }

        try {
            postDAO.insert(logged.getUserId(), content);
            response.sendRedirect(request.getContextPath() + "/feed?posted=1");
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("too long")) {
                response.sendRedirect(request.getContextPath() + "/feed?error=len");
            } else {
                throw new ServletException(e);
            }
        }
    }

    private void handleDeletePost(HttpServletRequest request, HttpServletResponse response, long userId)
            throws IOException, ServletException {
        String ctx = request.getContextPath();
        String idStr = request.getParameter("postId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/feed");
            return;
        }
        try {
            long postId = Long.parseLong(idStr);
            if (!postDAO.deleteIfAuthor(postId, userId)) {
                response.sendRedirect(ctx + "/feed?error=delpost");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/feed");
            return;
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(ctx + "/feed?deleted=1");
    }
}
