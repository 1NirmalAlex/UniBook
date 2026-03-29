package com.unibook.controller;

import com.unibook.dao.AchievementDAO;
import com.unibook.dao.UserDAO;
import com.unibook.model.Achievement;
import com.unibook.model.User;
import com.unibook.util.PasswordUtil;
import com.unibook.util.ValidationUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/profile")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AchievementDAO achievementDAO = new AchievementDAO();

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
            loadAndForward(request, response, session, logged.getUserId());
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
        long userId = logged.getUserId();

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            try {
                handlePhotoUpload(request, session, userId);
                response.sendRedirect(request.getContextPath() + "/profile?photo=ok");
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        try {
            switch (action) {
                case "updateProfile":
                    updateProfile(request, response, session, userId);
                    break;
                case "addAchievement":
                    addAchievement(request, response, userId);
                    break;
                case "deleteAchievement":
                    deleteAchievement(request, response, userId);
                    break;
                case "changePassword":
                    changePassword(request, response, userId);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/profile");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void loadAndForward(HttpServletRequest request, HttpServletResponse response, HttpSession session, long userId)
            throws SQLException, ServletException, IOException {
        User fresh = userDAO.findById(userId).orElse(null);
        if (fresh == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        session.setAttribute("loggedUser", fresh.safeCopyForSession());
        request.setAttribute("profileUser", fresh);
        request.setAttribute("achievements", achievementDAO.findByUserId(userId));
        request.setAttribute("navActive", "profile");
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, long userId) throws SQLException, IOException {
        String first = request.getParameter("firstName");
        String last = request.getParameter("lastName");
        String campus = request.getParameter("universityCampus");
        String degree = request.getParameter("degreeProgram");
        if (ValidationUtil.isBlank(first) || ValidationUtil.isBlank(last)
                || ValidationUtil.isBlank(campus) || ValidationUtil.isBlank(degree)) {
            response.sendRedirect(request.getContextPath() + "/profile?error=fields");
            return;
        }
        userDAO.updateProfile(userId, first, last, campus, degree);
        userDAO.findById(userId).ifPresent(u -> session.setAttribute("loggedUser", u.safeCopyForSession()));
        response.sendRedirect(request.getContextPath() + "/profile?updated=1");
    }

    private void addAchievement(HttpServletRequest request, HttpServletResponse response, long userId)
            throws SQLException, IOException {
        String title = request.getParameter("title");
        if (ValidationUtil.isBlank(title) || title.trim().length() > 255) {
            response.sendRedirect(request.getContextPath() + "/profile?error=achievement");
            return;
        }
        achievementDAO.insert(userId, title);
        response.sendRedirect(request.getContextPath() + "/profile?achievement=1");
    }

    private void deleteAchievement(HttpServletRequest request, HttpServletResponse response, long userId)
            throws SQLException, IOException {
        String idStr = request.getParameter("achievementId");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }
        try {
            long aid = Long.parseLong(idStr);
            achievementDAO.delete(aid, userId);
        } catch (NumberFormatException ignored) {
        }
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    private void handlePhotoUpload(HttpServletRequest request, HttpSession session, long userId)
            throws SQLException, IOException, ServletException {
        Part part = request.getPart("photo");
        if (part == null || part.getSize() == 0) {
            return;
        }
        String submitted = part.getSubmittedFileName();
        String ext = "";
        if (submitted != null && submitted.contains(".")) {
            ext = submitted.substring(submitted.lastIndexOf('.')).toLowerCase();
        }
        if (!ext.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            ext = ".jpg";
        }
        String ctype = part.getContentType();
        if (ctype == null || !ctype.startsWith("image/")) {
            return;
        }

        String dir = getServletContext().getRealPath("/uploads/profiles");
        if (dir == null) {
            throw new IOException("Cannot resolve upload path.");
        }
        Path dirPath = Paths.get(dir);
        Files.createDirectories(dirPath);

        String fileName = userId + "_" + UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = dirPath.resolve(fileName);
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String relative = "uploads/profiles/" + fileName;
        userDAO.updateProfilePicturePath(userId, relative);
        userDAO.findById(userId).ifPresent(u -> session.setAttribute("loggedUser", u.safeCopyForSession()));
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response, long userId)
            throws SQLException, IOException {
        String ctx = request.getContextPath();
        String current = request.getParameter("currentPassword");
        String next = request.getParameter("newPassword");
        String confirm = request.getParameter("confirmNewPassword");
        if (ValidationUtil.isBlank(current) || ValidationUtil.isBlank(next) || ValidationUtil.isBlank(confirm)) {
            response.sendRedirect(ctx + "/profile?pwd=empty");
            return;
        }
        if (!next.equals(confirm)) {
            response.sendRedirect(ctx + "/profile?pwd=mismatch");
            return;
        }
        if (next.length() < 6) {
            response.sendRedirect(ctx + "/profile?pwd=short");
            return;
        }
        if (next.length() > 128) {
            response.sendRedirect(ctx + "/profile?pwd=long");
            return;
        }
        Optional<String> hashOpt = userDAO.findPasswordHashByUserId(userId);
        if (hashOpt.isEmpty() || !PasswordUtil.verifyPassword(current, hashOpt.get())) {
            response.sendRedirect(ctx + "/profile?pwd=badcurrent");
            return;
        }
        userDAO.updatePassword(userId, next);
        response.sendRedirect(ctx + "/profile?pwd=ok");
    }
}
