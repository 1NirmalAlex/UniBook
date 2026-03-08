<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.unibook.model.User"%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f8f9fa;
            text-align: center;
            padding-top: 80px;
        }
        .box {
            width: 600px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    <div class="box">
        <%
            User user = (User) session.getAttribute("loggedUser");
            if (user != null) {
        %>
            <h1>Welcome to UniBook</h1>
            <p>Hello, <strong><%= user.getEmail() %></strong></p>
            <p>Login successful.</p>
        <%
            } else {
        %>
            <h1>No active session found.</h1>
        <%
            }
        %>
    </div>
</body>
</html>