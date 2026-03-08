package com.unibook.controller;

import com.unibook.model.Campus;
import com.unibook.service.CampusService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/registerCampus")
public class CampusServlet extends HttpServlet {

    private final CampusService campusService = new CampusService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String campusName = request.getParameter("campusName");
        String campusCode = request.getParameter("campusCode");
        String location = request.getParameter("location");

        Campus campus = new Campus(campusName, campusCode, location);

        if (campusService.registerCampus(campus)) {
            HttpSession session = request.getSession();
            session.setAttribute("registeredCampus", campus);
            session.setAttribute("campusRegistered", true);

            response.sendRedirect("login.jsp");
        } else {
            request.setAttribute("errorMessage", "Campus registration failed. Please fill all fields.");
            request.getRequestDispatcher("campusRegistration.jsp").forward(request, response);
        }
    }
}