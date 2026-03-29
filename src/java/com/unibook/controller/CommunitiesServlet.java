package com.unibook.controller;

import com.unibook.dao.CommunityDAO;
import com.unibook.model.Community;
import com.unibook.model.CommunityMember;
import com.unibook.model.User;
import com.unibook.util.ValidationUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/communities")
public class CommunitiesServlet extends HttpServlet {

    private static final int LIST_LIMIT = 200;

    private final CommunityDAO communityDAO = new CommunityDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String membersParam = request.getParameter("members");
        if (!ValidationUtil.isBlank(membersParam)) {
            try {
                long cid = Long.parseLong(membersParam.trim());
                handleMembersView(request, response, logged.getUserId(), cid);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/communities");
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            return;
        }

        try {
            request.setAttribute("communitiesList", communityDAO.findAllForViewer(logged.getUserId(), LIST_LIMIT));
            request.setAttribute("navActive", "communities");
            request.setAttribute("viewMode", "list");
            request.getRequestDispatcher("/communities.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void handleMembersView(HttpServletRequest request, HttpServletResponse response, long viewerId, long communityId)
            throws ServletException, IOException, SQLException {
        Optional<Community> opt = communityDAO.findById(communityId, viewerId);
        if (opt.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/communities?error=missing");
            return;
        }
        List<CommunityMember> members = communityDAO.findMembers(communityId);
        request.setAttribute("communityDetail", opt.get());
        request.setAttribute("communityMembers", members);
        request.setAttribute("navActive", "communities");
        request.setAttribute("viewMode", "members");
        request.getRequestDispatcher("/communities.jsp").forward(request, response);
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
            response.sendRedirect(ctx + "/communities");
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
                    response.sendRedirect(ctx + "/communities");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        if (ValidationUtil.isBlank(name)) {
            response.sendRedirect(ctx + "/communities?error=fields");
            return;
        }
        try {
            communityDAO.createCommunity(userId, name, description);
            response.sendRedirect(ctx + "/communities?created=1");
        } catch (SQLException e) {
            if (e.getMessage() != null && (e.getMessage().contains("too long"))) {
                response.sendRedirect(ctx + "/communities?error=len");
            } else {
                throw e;
            }
        }
    }

    private void handleJoin(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("communityId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/communities");
            return;
        }
        try {
            long cid = Long.parseLong(idStr);
            if (!communityDAO.joinCommunity(cid, userId)) {
                response.sendRedirect(ctx + "/communities?error=join");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/communities");
            return;
        }
        response.sendRedirect(ctx + "/communities?joined=1");
    }

    private void handleLeave(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("communityId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/communities");
            return;
        }
        try {
            long cid = Long.parseLong(idStr);
            int r = communityDAO.leaveCommunity(cid, userId);
            if (r == -1) {
                response.sendRedirect(ctx + "/communities?error=owner");
                return;
            }
            if (r == 0) {
                response.sendRedirect(ctx + "/communities?error=leave");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/communities");
            return;
        }
        response.sendRedirect(ctx + "/communities?left=1");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response, long userId, String ctx)
            throws SQLException, IOException {
        String idStr = request.getParameter("communityId");
        if (idStr == null) {
            response.sendRedirect(ctx + "/communities");
            return;
        }
        try {
            long cid = Long.parseLong(idStr);
            if (!communityDAO.deleteIfCreator(cid, userId)) {
                response.sendRedirect(ctx + "/communities?error=del");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(ctx + "/communities");
            return;
        }
        response.sendRedirect(ctx + "/communities?deleted=1");
    }
}
