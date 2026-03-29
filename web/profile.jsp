<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.unibook.model.User"%>
<%@page import="com.unibook.model.Achievement"%>
<%@page import="java.util.List"%>
<%@ include file="/includes/authCheck.jspf" %>
<%
    User profileUser = (User) request.getAttribute("profileUser");
    if (profileUser == null) {
        response.sendRedirect(request.getContextPath() + "/profile");
        return;
    }
    @SuppressWarnings("unchecked")
    List<Achievement> achievements = (List<Achievement>) request.getAttribute("achievements");
    String c = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>My Profile - UniBook</title>
    <link rel="stylesheet" href="<%= c %>/css/app.css"/>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #F8FAFC, #EAF3FF);
            min-height: 100vh;
            color: #1E293B;
        }
        .profile-hero {
            display: flex;
            gap: 18px;
            align-items: flex-start;
            padding: 22px;
            margin-bottom: 18px;
        }
        .avatar-lg {
            width: 96px;
            height: 96px;
            border-radius: 50%;
            object-fit: cover;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 36px;
            font-weight: 800;
            flex-shrink: 0;
            border: 4px solid #fff;
            box-shadow: 0 10px 28px rgba(37, 99, 235, 0.2);
        }
        .profile-hero h1 { font-size: 22px; margin-bottom: 6px; color: #0f172a; }
        .profile-hero .email { font-size: 14px; color: #64748B; word-break: break-all; }
        label { display: block; font-size: 13px; font-weight: 700; margin-bottom: 6px; color: #1E293B; }
        input[type="text"], input[type="email"], input[type="password"] {
            width: 100%; padding: 12px 14px; border: 1px solid #CBD5E1; border-radius: 14px;
            font-size: 15px; margin-bottom: 14px;
        }
        input:focus { outline: none; border-color: #60A5FA; box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2); }
        .section-title { font-size: 17px; font-weight: 800; color: #2563EB; margin: 8px 0 14px; }
        .btn {
            border: none; border-radius: 14px; padding: 12px 18px; font-weight: 700; cursor: pointer;
            font-size: 15px;
        }
        .btn-primary {
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            color: #fff;
            width: 100%;
        }
        .btn-ghost {
            background: #F1F5F9;
            color: #475569;
            font-size: 13px;
            padding: 8px 12px;
        }
        .achievement-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 14px;
            border-radius: 14px;
            background: #F8FAFC;
            margin-bottom: 8px;
            border: 1px solid #E2E8F0;
        }
        .achievement-row span { font-weight: 600; color: #334155; font-size: 14px; }
        .photo-form { margin-top: 12px; }
        .photo-form input[type="file"] { font-size: 14px; margin-bottom: 10px; }
    </style>
</head>
<body class="app-body">

<div class="page-shell">
    <%@ include file="/includes/appTopbar.jspf" %>

    <% if ("1".equals(request.getParameter("updated"))) { %>
        <div class="flash ok">Profile updated.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("achievement"))) { %>
        <div class="flash ok">Achievement added.</div>
    <% } %>
    <% if ("ok".equals(request.getParameter("photo"))) { %>
        <div class="flash ok">Profile picture updated.</div>
    <% } %>
    <% if ("fields".equals(request.getParameter("error"))) { %>
        <div class="flash err">All profile fields are required.</div>
    <% } %>
    <% if ("achievement".equals(request.getParameter("error"))) { %>
        <div class="flash err">Enter a valid achievement title (max 255 characters).</div>
    <% } %>
    <% if ("ok".equals(request.getParameter("pwd"))) { %>
        <div class="flash ok">Password updated.</div>
    <% } %>
    <% if ("badcurrent".equals(request.getParameter("pwd"))) { %>
        <div class="flash err">Current password is incorrect.</div>
    <% } %>
    <% if ("mismatch".equals(request.getParameter("pwd"))) { %>
        <div class="flash err">New password and confirmation do not match.</div>
    <% } %>
    <% if ("short".equals(request.getParameter("pwd"))) { %>
        <div class="flash err">New password must be at least 6 characters.</div>
    <% } %>
    <% if ("long".equals(request.getParameter("pwd"))) { %>
        <div class="flash err">New password is too long (max 128 characters).</div>
    <% } %>
    <% if ("empty".equals(request.getParameter("pwd"))) { %>
        <div class="flash err">Fill in all password fields.</div>
    <% } %>

    <div class="glass-card profile-hero">
        <% if (profileUser.getProfilePicturePath() != null && !profileUser.getProfilePicturePath().isEmpty()) { %>
            <img class="avatar-lg" src="<%= c %>/<%= profileUser.getProfilePicturePath() %>" alt="Profile photo"/>
        <% } else { %>
            <div class="avatar-lg"><%= profileUser.getFirstName() != null && !profileUser.getFirstName().isEmpty()
                    ? profileUser.getFirstName().substring(0, 1).toUpperCase() : "U" %></div>
        <% } %>
        <div>
            <h1><%= profileUser.getFullName() %></h1>
            <p class="email"><%= profileUser.getEmail() %></p>
            <p style="margin-top:10px;font-size:14px;color:#475569;"><%= profileUser.getUniversityCampus() %> · <%= profileUser.getDegreeProgram() %></p>
        </div>
    </div>

    <div class="glass-card" style="padding:22px;margin-bottom:18px;">
        <h2 class="section-title">Edit profile</h2>
        <form action="<%= c %>/profile" method="post">
            <input type="hidden" name="action" value="updateProfile"/>
            <label for="firstName">First name</label>
            <input type="text" id="firstName" name="firstName" required value="<%= profileUser.getFirstName() == null ? "" : profileUser.getFirstName().replace("\"", "&quot;") %>"/>

            <label for="lastName">Last name</label>
            <input type="text" id="lastName" name="lastName" required value="<%= profileUser.getLastName() == null ? "" : profileUser.getLastName().replace("\"", "&quot;") %>"/>

            <label for="universityCampus">University / Campus</label>
            <input type="text" id="universityCampus" name="universityCampus" required value="<%= profileUser.getUniversityCampus() == null ? "" : profileUser.getUniversityCampus().replace("\"", "&quot;") %>"/>

            <label for="degreeProgram">Degree program</label>
            <input type="text" id="degreeProgram" name="degreeProgram" required value="<%= profileUser.getDegreeProgram() == null ? "" : profileUser.getDegreeProgram().replace("\"", "&quot;") %>"/>

            <button type="submit" class="btn btn-primary">Save changes</button>
        </form>
    </div>

    <div class="glass-card" style="padding:22px;margin-bottom:18px;">
        <h2 class="section-title">Change password</h2>
        <p style="font-size:14px;color:#64748B;margin-bottom:12px;">At least 6 characters. You must enter your current password.</p>
        <form action="<%= c %>/profile" method="post" autocomplete="off">
            <input type="hidden" name="action" value="changePassword"/>
            <label for="currentPassword">Current password</label>
            <input type="password" id="currentPassword" name="currentPassword" required autocomplete="current-password"/>

            <label for="newPassword">New password</label>
            <input type="password" id="newPassword" name="newPassword" required minlength="6" maxlength="128" autocomplete="new-password"/>

            <label for="confirmNewPassword">Confirm new password</label>
            <input type="password" id="confirmNewPassword" name="confirmNewPassword" required minlength="6" maxlength="128" autocomplete="new-password"/>

            <button type="submit" class="btn btn-primary">Update password</button>
        </form>
    </div>

    <div class="glass-card" style="padding:22px;margin-bottom:18px;">
        <h2 class="section-title">Profile picture</h2>
        <p style="font-size:14px;color:#64748B;margin-bottom:12px;">JPG, PNG, GIF, or WebP · max 5 MB</p>
        <form class="photo-form" action="<%= c %>/profile" method="post" enctype="multipart/form-data">
            <input type="file" name="photo" accept="image/*" required/>
            <button type="submit" class="btn btn-primary" style="width:auto;">Upload</button>
        </form>
    </div>

    <div class="glass-card" style="padding:22px;margin-bottom:18px;">
        <h2 class="section-title">Achievements</h2>
        <form action="<%= c %>/profile" method="post" style="margin-bottom:16px;">
            <input type="hidden" name="action" value="addAchievement"/>
            <label for="achTitle">Add achievement</label>
            <input type="text" id="achTitle" name="title" maxlength="255" placeholder="e.g. Dean's List 2025"/>
            <button type="submit" class="btn btn-primary">Add</button>
        </form>
        <% if (achievements != null && !achievements.isEmpty()) {
            for (Achievement a : achievements) { %>
            <div class="achievement-row">
                <span><%= a.getTitle() %></span>
                <form action="<%= c %>/profile" method="post" style="margin:0;">
                    <input type="hidden" name="action" value="deleteAchievement"/>
                    <input type="hidden" name="achievementId" value="<%= a.getAchievementId() %>"/>
                    <button type="submit" class="btn btn-ghost">Remove</button>
                </form>
            </div>
        <% } } else { %>
            <p style="color:#64748B;font-size:14px;">No achievements yet.</p>
        <% } %>
    </div>
</div>

<%@ include file="/includes/bottomNav.jspf" %>
</body>
</html>
