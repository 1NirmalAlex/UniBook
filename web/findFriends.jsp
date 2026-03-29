<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.unibook.model.User"%>
<%@page import="com.unibook.model.FriendRelation"%>
<%@page import="com.unibook.model.FriendRequest"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ include file="/includes/authCheck.jspf" %>
<%
    if (request.getAttribute("suggestions") == null) {
        response.sendRedirect(request.getContextPath() + "/friends");
        return;
    }
    @SuppressWarnings("unchecked")
    List<User> searchResults = (List<User>) request.getAttribute("searchResults");
    @SuppressWarnings("unchecked")
    Map<Long, FriendRelation> searchRelations = (Map<Long, FriendRelation>) request.getAttribute("searchRelations");
    @SuppressWarnings("unchecked")
    List<User> suggestions = (List<User>) request.getAttribute("suggestions");
    @SuppressWarnings("unchecked")
    Map<Long, FriendRelation> suggestRelations = (Map<Long, FriendRelation>) request.getAttribute("suggestRelations");
    @SuppressWarnings("unchecked")
    List<FriendRequest> incoming = (List<FriendRequest>) request.getAttribute("incomingRequests");
    @SuppressWarnings("unchecked")
    List<FriendRequest> outgoing = (List<FriendRequest>) request.getAttribute("outgoingRequests");
    String searchQuery = (String) request.getAttribute("searchQuery");
    if (searchQuery == null) {
        searchQuery = "";
    }
    String c = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Find Friends - UniBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="stylesheet" href="<%= c %>/css/app.css"/>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: "Segoe UI", Tahoma, sans-serif; background: linear-gradient(135deg, #F8FAFC, #EAF3FF); min-height: 100vh; color: #1E293B; }
        .page-shell { max-width: 520px; margin: 0 auto; padding: 18px 16px; }
        h1 { font-size: 22px; color: #2563EB; margin-bottom: 6px; }
        .sub { color: #64748B; font-size: 14px; margin-bottom: 18px; }
        .section { margin-bottom: 22px; }
        .section-title { font-size: 15px; font-weight: 800; color: #0f172a; margin-bottom: 10px; }
        .card { background: rgba(255, 255, 255, 0.9); border: 1px solid #e2e8f0; border-radius: 20px; padding: 16px; margin-bottom: 10px; box-shadow: 0 8px 28px rgba(37, 99, 235, 0.06); }
        .search-row { display: flex; gap: 8px; margin-bottom: 8px; }
        .search-row input[type="search"] { flex: 1; padding: 12px 14px; border: 1px solid #CBD5E1; border-radius: 14px; font-size: 15px; }
        .search-row button { padding: 12px 18px; border: none; border-radius: 14px; background: linear-gradient(135deg, #2563EB, #60A5FA); color: #fff; font-weight: 700; cursor: pointer; }
        .user-row { display: flex; align-items: center; gap: 12px; justify-content: space-between; flex-wrap: wrap; }
        .user-main { display: flex; align-items: center; gap: 12px; min-width: 0; flex: 1; }
        .avatar { width: 44px; height: 44px; border-radius: 50%; background: linear-gradient(135deg, #2563EB, #60A5FA); color: #fff; font-weight: 800; display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 16px; overflow: hidden; }
        .avatar img { width: 100%; height: 100%; object-fit: cover; }
        .user-text { min-width: 0; }
        .user-text .name { font-weight: 700; font-size: 15px; color: #0f172a; }
        .user-text .meta { font-size: 12px; color: #64748B; word-break: break-word; }
        .badge { display: inline-block; padding: 6px 10px; border-radius: 999px; font-size: 12px; font-weight: 700; background: #EFF6FF; color: #2563EB; }
        .badge.muted { background: #F1F5F9; color: #64748B; }
        .actions { display: flex; gap: 8px; flex-wrap: wrap; align-items: center; }
        form.inline { display: inline; margin: 0; }
        .btn { border: none; border-radius: 12px; padding: 8px 14px; font-size: 13px; font-weight: 700; cursor: pointer; }
        .btn-primary { background: linear-gradient(135deg, #2563EB, #60A5FA); color: #fff; }
        .btn-ghost { background: #F1F5F9; color: #475569; }
        .btn-danger { background: #FEF2F2; color: #B91C1C; }
        .flash { padding: 12px 14px; border-radius: 14px; font-size: 14px; font-weight: 600; margin-bottom: 14px; }
        .flash.ok { background: #ECFDF5; border: 1px solid #A7F3D0; color: #047857; }
        .flash.err { background: #FEF2F2; border: 1px solid #FECACA; color: #B91C1C; }
        .empty { color: #94A3B8; font-size: 14px; padding: 8px 0; }
    </style>
</head>
<body class="app-body">
<div class="page-shell">
    <%@ include file="/includes/appTopbar.jspf" %>

    <% if ("1".equals(request.getParameter("sent"))) { %>
        <div class="flash ok">Friend request sent.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("accepted"))) { %>
        <div class="flash ok">You are now friends.</div>
    <% } %>
    <% if ("send".equals(request.getParameter("error"))) { %>
        <div class="flash err">Could not send request. You may already be friends or have a pending request.</div>
    <% } %>
    <% if ("accept".equals(request.getParameter("error"))) { %>
        <div class="flash err">Could not accept that request.</div>
    <% } %>

    <h1>Find friends</h1>
    <p class="sub">Search by name or email · get suggestions from your campus</p>

    <div class="card section">
        <form class="search-row" method="get" action="<%= c %>/friends">
            <input type="search" name="q" placeholder="Search name or email…" value="<%= searchQuery.replace("\"", "&quot;") %>"/>
            <button type="submit">Search</button>
        </form>
    </div>

    <div class="section">
        <p class="section-title">Incoming requests</p>
        <% if (incoming == null || incoming.isEmpty()) { %>
            <div class="empty">No pending requests.</div>
        <% } else {
            for (FriendRequest fr : incoming) {
                User o = fr.getOtherUser();
        %>
            <div class="card user-row">
                <div class="user-main">
                    <div class="avatar">
                        <% if (o != null && o.getProfilePicturePath() != null && !o.getProfilePicturePath().isEmpty()) { %>
                            <img src="<%= c %>/<%= o.getProfilePicturePath() %>" alt=""/>
                        <% } else { %>
                            <%= o != null && o.getFirstName() != null && !o.getFirstName().isEmpty() ? o.getFirstName().substring(0, 1).toUpperCase() : "?" %>
                        <% } %>
                    </div>
                    <div class="user-text">
                        <div class="name"><%= o != null ? o.getFullName() : "" %></div>
                        <div class="meta"><%= o != null ? o.getUniversityCampus() : "" %></div>
                    </div>
                </div>
                <div class="actions">
                    <form class="inline" method="post" action="<%= c %>/friends">
                        <input type="hidden" name="action" value="accept"/>
                        <input type="hidden" name="requestId" value="<%= fr.getRequestId() %>"/>
                        <button type="submit" class="btn btn-primary">Accept</button>
                    </form>
                    <form class="inline" method="post" action="<%= c %>/friends">
                        <input type="hidden" name="action" value="reject"/>
                        <input type="hidden" name="requestId" value="<%= fr.getRequestId() %>"/>
                        <button type="submit" class="btn btn-danger">Decline</button>
                    </form>
                </div>
            </div>
        <% } } %>
    </div>

    <div class="section">
        <p class="section-title">Sent requests</p>
        <% if (outgoing == null || outgoing.isEmpty()) { %>
            <div class="empty">No outgoing requests.</div>
        <% } else {
            for (FriendRequest fr : outgoing) {
                User o = fr.getOtherUser();
        %>
            <div class="card user-row">
                <div class="user-main">
                    <div class="avatar">
                        <% if (o != null && o.getProfilePicturePath() != null && !o.getProfilePicturePath().isEmpty()) { %>
                            <img src="<%= c %>/<%= o.getProfilePicturePath() %>" alt=""/>
                        <% } else { %>
                            <%= o != null && o.getFirstName() != null && !o.getFirstName().isEmpty() ? o.getFirstName().substring(0, 1).toUpperCase() : "?" %>
                        <% } %>
                    </div>
                    <div class="user-text">
                        <div class="name"><%= o != null ? o.getFullName() : "" %></div>
                        <div class="meta">Pending</div>
                    </div>
                </div>
                <form class="inline" method="post" action="<%= c %>/friends">
                    <input type="hidden" name="action" value="cancel"/>
                    <input type="hidden" name="requestId" value="<%= fr.getRequestId() %>"/>
                    <button type="submit" class="btn btn-ghost">Cancel</button>
                </form>
            </div>
        <% } } %>
    </div>

    <div class="section">
        <p class="section-title">Suggested (same campus)</p>
        <% if (suggestions == null || suggestions.isEmpty()) { %>
            <div class="empty">No suggestions right now.</div>
        <% } else {
            for (User u : suggestions) {
                FriendRelation rel = suggestRelations != null ? suggestRelations.get(u.getUserId()) : FriendRelation.NONE;
        %>
            <div class="card user-row">
                <div class="user-main">
                    <div class="avatar">
                        <% if (u.getProfilePicturePath() != null && !u.getProfilePicturePath().isEmpty()) { %>
                            <img src="<%= c %>/<%= u.getProfilePicturePath() %>" alt=""/>
                        <% } else { %>
                            <%= u.getFirstName() != null && !u.getFirstName().isEmpty() ? u.getFirstName().substring(0, 1).toUpperCase() : "?" %>
                        <% } %>
                    </div>
                    <div class="user-text">
                        <div class="name"><%= u.getFullName() %></div>
                        <div class="meta"><%= u.getEmail() %></div>
                    </div>
                </div>
                <div class="actions">
                    <% if (rel == FriendRelation.FRIEND) { %>
                        <span class="badge">Friends</span>
                    <% } else if (rel == FriendRelation.REQUEST_SENT) { %>
                        <span class="badge muted">Request sent</span>
                        <form class="inline" method="post" action="<%= c %>/friends">
                            <input type="hidden" name="action" value="cancel"/>
                            <input type="hidden" name="targetUserId" value="<%= u.getUserId() %>"/>
                            <button type="submit" class="btn btn-ghost">Cancel</button>
                        </form>
                    <% } else if (rel == FriendRelation.REQUEST_RECEIVED) { %>
                        <span class="badge muted">Wants to connect</span>
                    <% } else { %>
                        <form class="inline" method="post" action="<%= c %>/friends">
                            <input type="hidden" name="action" value="send"/>
                            <input type="hidden" name="targetUserId" value="<%= u.getUserId() %>"/>
                            <button type="submit" class="btn btn-primary">Add friend</button>
                        </form>
                    <% } %>
                </div>
            </div>
        <% } } %>
    </div>

    <% if (searchQuery != null && !searchQuery.trim().isEmpty()) { %>
    <div class="section">
        <p class="section-title">Search results</p>
        <% if (searchResults == null || searchResults.isEmpty()) { %>
            <div class="empty">No users match “<%= searchQuery.replace("\"", "&quot;") %>”.</div>
        <% } else {
            for (User u : searchResults) {
                FriendRelation rel = searchRelations != null ? searchRelations.get(u.getUserId()) : FriendRelation.NONE;
        %>
            <div class="card user-row">
                <div class="user-main">
                    <div class="avatar">
                        <% if (u.getProfilePicturePath() != null && !u.getProfilePicturePath().isEmpty()) { %>
                            <img src="<%= c %>/<%= u.getProfilePicturePath() %>" alt=""/>
                        <% } else { %>
                            <%= u.getFirstName() != null && !u.getFirstName().isEmpty() ? u.getFirstName().substring(0, 1).toUpperCase() : "?" %>
                        <% } %>
                    </div>
                    <div class="user-text">
                        <div class="name"><%= u.getFullName() %></div>
                        <div class="meta"><%= u.getEmail() %> · <%= u.getUniversityCampus() %></div>
                    </div>
                </div>
                <div class="actions">
                    <% if (rel == FriendRelation.FRIEND) { %>
                        <span class="badge">Friends</span>
                    <% } else if (rel == FriendRelation.REQUEST_SENT) { %>
                        <span class="badge muted">Request sent</span>
                        <form class="inline" method="post" action="<%= c %>/friends">
                            <input type="hidden" name="action" value="cancel"/>
                            <input type="hidden" name="targetUserId" value="<%= u.getUserId() %>"/>
                            <input type="hidden" name="q" value="<%= searchQuery.replace("\"", "&quot;") %>"/>
                            <button type="submit" class="btn btn-ghost">Cancel</button>
                        </form>
                    <% } else if (rel == FriendRelation.REQUEST_RECEIVED) { %>
                        <span class="badge muted">Incoming request</span>
                    <% } else { %>
                        <form class="inline" method="post" action="<%= c %>/friends">
                            <input type="hidden" name="action" value="send"/>
                            <input type="hidden" name="targetUserId" value="<%= u.getUserId() %>"/>
                            <input type="hidden" name="q" value="<%= searchQuery.replace("\"", "&quot;") %>"/>
                            <button type="submit" class="btn btn-primary">Add friend</button>
                        </form>
                    <% } %>
                </div>
            </div>
        <% } } %>
    </div>
    <% } %>

</div>
<% request.setAttribute("navActive", "friends"); %>
<%@ include file="/includes/bottomNav.jspf" %>
</body>
</html>
