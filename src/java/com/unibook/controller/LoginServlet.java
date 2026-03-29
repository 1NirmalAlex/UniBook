package com.unibook.controller;

import com.unibook.dao.UserDAO;
import com.unibook.model.User;
import com.unibook.util.LoginRedirectUtil;
import com.unibook.util.PasswordUtil;
import com.unibook.util.ValidationUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/loginUser")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) {
            request.setAttribute("errorMessage", "Email and password are required.");
            request.setAttribute("loginNext", request.getParameter("next"));
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            Optional<User> opt = userDAO.findByEmailWithHash(email);
            if (opt.isEmpty()) {
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.setAttribute("loginNext", request.getParameter("next"));
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            User withHash = opt.get();
            if (!PasswordUtil.verifyPassword(password, withHash.getPasswordHash())) {
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.setAttribute("loginNext", request.getParameter("next"));
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            Optional<User> full = userDAO.findById(withHash.getUserId());
            User sessionUser = full.orElse(withHash).safeCopyForSession();

            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", sessionUser);

            response.sendRedirect(LoginRedirectUtil.safePostLoginRedirect(request.getParameter("next"),
                    request.getContextPath()));
        } catch (SQLException e) {
            throw new ServletException("Database error during login.", e);
        }
    }
}
