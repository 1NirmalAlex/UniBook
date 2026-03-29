package com.unibook.controller;

import com.unibook.dao.UserDAO;
import com.unibook.model.User;
import com.unibook.util.ValidationUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/signup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String universityCampus = request.getParameter("universityCampus");
        String degreeProgram = request.getParameter("degreeProgram");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (ValidationUtil.isBlank(firstName) || ValidationUtil.isBlank(lastName)
                || ValidationUtil.isBlank(universityCampus) || ValidationUtil.isBlank(degreeProgram)
                || ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)
                || ValidationUtil.isBlank(confirmPassword)) {
            request.setAttribute("errorMessage", "All fields are required.");
            copyFormToRequest(request, firstName, lastName, universityCampus, degreeProgram, email);
            request.getRequestDispatcher("/signup.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidCampusEmail(email)) {
            request.setAttribute("errorMessage", "Campus email must end with \"edu.lk\".");
            copyFormToRequest(request, firstName, lastName, universityCampus, degreeProgram, email);
            request.getRequestDispatcher("/signup.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Password and confirm password do not match.");
            copyFormToRequest(request, firstName, lastName, universityCampus, degreeProgram, email);
            request.getRequestDispatcher("/signup.jsp").forward(request, response);
            return;
        }

        try {
            if (userDAO.emailExists(email)) {
                request.setAttribute("errorMessage", "An account with this email already exists.");
                copyFormToRequest(request, firstName, lastName, universityCampus, degreeProgram, email);
                request.getRequestDispatcher("/signup.jsp").forward(request, response);
                return;
            }

            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUniversityCampus(universityCampus);
            user.setDegreeProgram(degreeProgram);
            user.setEmail(email.trim().toLowerCase());

            userDAO.insertUser(user, password);
            response.sendRedirect(request.getContextPath() + "/login.jsp?registered=1");
        } catch (SQLException e) {
            throw new ServletException("Database error while registering user.", e);
        }
    }

    private void copyFormToRequest(HttpServletRequest request, String firstName, String lastName,
            String universityCampus, String degreeProgram, String email) {
        request.setAttribute("firstName", firstName != null ? firstName : "");
        request.setAttribute("lastName", lastName != null ? lastName : "");
        request.setAttribute("universityCampus", universityCampus != null ? universityCampus : "");
        request.setAttribute("degreeProgram", degreeProgram != null ? degreeProgram : "");
        request.setAttribute("email", email != null ? email : "");
    }
}
