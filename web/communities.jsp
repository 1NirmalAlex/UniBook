<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.unibook.model.User"%>
<%@page import="com.unibook.model.Community"%>
<%@page import="com.unibook.model.CommunityMember"%>
<%@page import="com.unibook.util.HtmlEscape"%>
<%@page import="java.util.List"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@ include file="/includes/authCheck.jspf" %>
<%
    String viewMode = (String) request.getAttribute("viewMode");
    if (viewMode == null) {
        response.sendRedirect(request.getContextPath() + "/communities");
        return;
    }
    String c = request.getContextPath();
    DateTimeFormatter joinedFmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Communities - UniBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="stylesheet" href="<%= c %>/css/app.css"/>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: "Segoe UI", Tahoma, sans-serif; background: linear-gradient(135deg, #F8FAFC, #EAF3FF); min-height: 100vh; color: #1E293B; }
        .page-shell { max-width: 520px; margin: 0 auto; padding: 18px 16px; }
        h1 { font-size: 22px; color: #2563EB; margin-bottom: 6px; }
        .sub { color: #64748B; font-size: 14px; margin-bottom: 18px; line-height: 1.5; }
        .card { background: rgba(255, 255, 255, 0.9); border: 1px solid #e2e8f0; border-radius: 20px; padding: 18px; margin-bottom: 14px; box-shadow: 0 8px 28px rgba(37, 99, 235, 0.06); }
        label { display: block; font-size: 13px; font-weight: 700; margin-bottom: 6px; color: #1E293B; }
        input[type="text"], textarea {
            width: 100%; padding: 12px 14px; border: 1px solid #CBD5E1; border-radius: 14px; font-size: 15px; margin-bottom: 12px; font-family: inherit;
        }
        textarea { min-height: 88px; resize: vertical; }
        .btn { border: none; border-radius: 14px; padding: 10px 16px; font-weight: 700; font-size: 14px; cursor: pointer; text-decoration: none; display: inline-block; }
        .btn-primary { background: linear-gradient(135deg, #2563EB, #60A5FA); color: #fff; }
        .btn-ghost { background: #F1F5F9; color: #475569; }
        .btn-danger { background: #FEF2F2; color: #B91C1C; }
        .btn-block { width: 100%; text-align: center; }
        .co-name { font-size: 17px; font-weight: 800; color: #0f172a; margin-bottom: 6px; }
        .co-desc { font-size: 14px; color: #475569; line-height: 1.55; margin-bottom: 10px; white-space: pre-wrap; }
        .co-meta { font-size: 12px; color: #94A3B8; margin-bottom: 12px; }
        .badge { display: inline-block; padding: 5px 10px; border-radius: 999px; font-size: 11px; font-weight: 700; background: #EFF6FF; color: #2563EB; margin-right: 6px; }
        .badge.ok { background: #ECFDF5; color: #047857; }
        .actions { display: flex; gap: 8px; flex-wrap: wrap; align-items: center; }
        form.inline { display: inline; margin: 0; }
        .member-row { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid #f1f5f9; }
        .member-row:last-child { border-bottom: none; }
        .avatar { width: 40px; height: 40px; border-radius: 50%; background: linear-gradient(135deg, #2563EB, #60A5FA); color: #fff; font-weight: 800; display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 15px; overflow: hidden; }
        .avatar img { width: 100%; height: 100%; object-fit: cover; }
        .member-text { min-width: 0; flex: 1; }
        .member-text .n { font-weight: 700; font-size: 15px; }
        .member-text .e { font-size: 12px; color: #64748B; word-break: break-all; }
        .member-text .j { font-size: 11px; color: #94A3B8; margin-top: 2px; }
    </style>
</head>
<body class="app-body">
<div class="page-shell">
    <%@ include file="/includes/appTopbar.jspf" %>

<% if ("list".equals(viewMode)) {
    @SuppressWarnings("unchecked")
    List<Community> communitiesList = (List<Community>) request.getAttribute("communitiesList");
%>
    <% if ("1".equals(request.getParameter("created"))) { %>
        <div class="flash ok">Community created.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("joined"))) { %>
        <div class="flash ok">You joined the community.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("left"))) { %>
        <div class="flash ok">You left the community.</div>
    <% } %>
    <% if ("fields".equals(request.getParameter("error"))) { %>
        <div class="flash err">Community name is required.</div>
    <% } %>
    <% if ("len".equals(request.getParameter("error"))) { %>
        <div class="flash err">Name or description is too long.</div>
    <% } %>
    <% if ("join".equals(request.getParameter("error"))) { %>
        <div class="flash err">Could not join (already a member?).</div>
    <% } %>
    <% if ("owner".equals(request.getParameter("error"))) { %>
        <div class="flash err">Community owners stay in the member list.</div>
    <% } %>
    <% if ("leave".equals(request.getParameter("error"))) { %>
        <div class="flash err">You are not a member of that community.</div>
    <% } %>
    <% if ("missing".equals(request.getParameter("error"))) { %>
        <div class="flash err">That community was not found.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("deleted"))) { %>
        <div class="flash ok">Community deleted.</div>
    <% } %>
    <% if ("del".equals(request.getParameter("error"))) { %>
        <div class="flash err">Only the owner can delete that community.</div>
    <% } %>

    <h1>Communities</h1>
    <p class="sub">Create a group, join others, and see who’s in each community.</p>

    <div class="card">
        <h2 style="font-size:16px;margin-bottom:12px;color:#0f172a;">Create community</h2>
        <form action="<%= c %>/communities" method="post">
            <input type="hidden" name="action" value="create"/>
            <label for="name">Name</label>
            <input type="text" id="name" name="name" maxlength="200" required placeholder="Study group, club, batch…"/>

            <label for="description">Description <span style="font-weight:400;color:#94A3B8;">(optional)</span></label>
            <textarea id="description" name="description" maxlength="4000" placeholder="What is this community about?"></textarea>

            <button type="submit" class="btn btn-primary btn-block">Create</button>
        </form>
    </div>

    <h2 style="font-size:16px;margin:22px 0 12px;color:#0f172a;">All communities</h2>
    <% if (communitiesList == null || communitiesList.isEmpty()) { %>
        <div class="card" style="text-align:center;color:#64748B;">No communities yet. Create one above.</div>
    <% } else {
        for (Community co : communitiesList) {
            User creator = co.getCreator();
            String creatorName = creator != null ? creator.getFullName() : "Someone";
            boolean isOwner = co.getCreatorId() == loggedUser.getUserId();
    %>
        <div class="card">
            <div class="co-name"><%= HtmlEscape.escape(co.getName()) %></div>
            <% if (co.getDescription() != null && !co.getDescription().trim().isEmpty()) { %>
                <div class="co-desc"><%= HtmlEscape.escape(co.getDescription()) %></div>
            <% } %>
            <div class="co-meta">
                Owner: <%= HtmlEscape.escape(creatorName) %> · <%= co.getMemberCount() %> members
                · <a href="<%= c %>/communities?members=<%= co.getCommunityId() %>" style="color:#2563EB;font-weight:600;">View members</a>
            </div>
            <div class="actions">
                <% if (isOwner) { %>
                    <span class="badge ok">Your community</span>
                    <form class="inline" method="post" action="<%= c %>/communities" onsubmit="return confirm('Delete this community and remove all members?');">
                        <input type="hidden" name="action" value="delete"/>
                        <input type="hidden" name="communityId" value="<%= co.getCommunityId() %>"/>
                        <button type="submit" class="btn btn-danger">Delete community</button>
                    </form>
                <% } %>
                <% if (co.isJoinedByCurrentUser()) { %>
                    <span class="badge">Member</span>
                    <% if (!isOwner) { %>
                    <form class="inline" method="post" action="<%= c %>/communities">
                        <input type="hidden" name="action" value="leave"/>
                        <input type="hidden" name="communityId" value="<%= co.getCommunityId() %>"/>
                        <button type="submit" class="btn btn-danger">Leave</button>
                    </form>
                    <% } %>
                <% } else { %>
                    <form class="inline" method="post" action="<%= c %>/communities">
                        <input type="hidden" name="action" value="join"/>
                        <input type="hidden" name="communityId" value="<%= co.getCommunityId() %>"/>
                        <button type="submit" class="btn btn-primary">Join</button>
                    </form>
                <% } %>
            </div>
        </div>
    <% } } %>

<% } else if ("members".equals(viewMode)) {
    Community detail = (Community) request.getAttribute("communityDetail");
    @SuppressWarnings("unchecked")
    List<CommunityMember> communityMembers = (List<CommunityMember>) request.getAttribute("communityMembers");
    if (detail == null) {
        response.sendRedirect(c + "/communities");
        return;
    }
%>
    <p style="margin-bottom:14px;"><a href="<%= c %>/communities" style="color:#2563EB;font-weight:600;text-decoration:none;">← Back to communities</a></p>
    <h1><%= HtmlEscape.escape(detail.getName()) %></h1>
    <p class="sub">Members · <%= detail.getMemberCount() %> total</p>
    <% if (detail.getDescription() != null && !detail.getDescription().trim().isEmpty()) { %>
        <div class="card co-desc" style="margin-bottom:16px;"><%= HtmlEscape.escape(detail.getDescription()) %></div>
    <% } %>

    <% if (detail.getCreatorId() == loggedUser.getUserId()) { %>
    <div class="card" style="margin-bottom:14px;">
        <form method="post" action="<%= c %>/communities" onsubmit="return confirm('Delete this community and all memberships?');">
            <input type="hidden" name="action" value="delete"/>
            <input type="hidden" name="communityId" value="<%= detail.getCommunityId() %>"/>
            <button type="submit" class="btn btn-danger" style="width:100%;">Delete community</button>
        </form>
    </div>
    <% } %>

    <div class="card">
        <% if (communityMembers == null || communityMembers.isEmpty()) { %>
            <p style="color:#64748B;text-align:center;padding:12px;">No members listed.</p>
        <% } else {
            for (CommunityMember m : communityMembers) {
                User u = m.getUser();
                if (u == null) continue;
                String initial = u.getFirstName() != null && !u.getFirstName().isEmpty()
                        ? u.getFirstName().substring(0, 1).toUpperCase() : "?";
                String j = m.getJoinedAt() != null ? "Joined " + m.getJoinedAt().format(joinedFmt) : "";
        %>
            <div class="member-row">
                <div class="avatar">
                    <% if (u.getProfilePicturePath() != null && !u.getProfilePicturePath().isEmpty()) { %>
                        <img src="<%= c %>/<%= u.getProfilePicturePath() %>" alt=""/>
                    <% } else { %>
                        <%= initial %>
                    <% } %>
                </div>
                <div class="member-text">
                    <div class="n"><%= HtmlEscape.escape(u.getFullName()) %></div>
                    <div class="e"><%= HtmlEscape.escape(u.getEmail()) %></div>
                    <div class="j"><%= HtmlEscape.escape(j) %></div>
                </div>
            </div>
        <% } } %>
    </div>
<% } %>

</div>
<% request.setAttribute("navActive", "communities"); %>
<%@ include file="/includes/bottomNav.jspf" %>
</body>
</html>
