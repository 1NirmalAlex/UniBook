package com.unibook.service;

import com.unibook.model.User;

public class UserService {

    public String decideNextPage(String userType) {
        if (userType == null) {
            return "error.jsp";
        }

        switch (userType) {
            case "new_without_campus":
                return "campusRegistration.jsp";
            case "new_with_campus":
                return "login.jsp";
            case "already_registered":
                return "login.jsp";
            default:
                return "error.jsp";
        }
    }

    public boolean validateLogin(String email, String password) {
        return email != null && !email.trim().isEmpty()
                && password != null && !password.trim().isEmpty();
    }

    public User createDemoUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setFullName("Demo User");
        user.setAlreadyRegistered(true);
        user.setCampusRegistered(true);
        return user;
    }
}