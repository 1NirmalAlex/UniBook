<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.unibook.model.User"%>
<%@page import="com.unibook.model.KuppiyaSession"%>
<%@page import="com.unibook.util.HtmlEscape"%>
<%@page import="java.util.List"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@ include file="/includes/authCheck.jspf" %>
<%
    if (request.getAttribute("kuppiyaSessions") == null) {
        response.sendRedirect(request.getContextPath() + "/kuppiya");
        return;
    }
    @SuppressWarnings("unchecked")
    List<KuppiyaSession> kuppiyaSessions = (List<KuppiyaSession>) request.getAttribute("kuppiyaSessions");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy 'at' h:mm a");
    String c = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Kuppiya - UniBook</title>
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
        input[type="text"], input[type="datetime-local"], textarea {
            width: 100%; padding: 12px 14px; border: 1px solid #CBD5E1; border-radius: 14px; font-size: 15px; margin-bottom: 12px; font-family: inherit;
        }
        textarea { min-height: 90px; resize: vertical; }
        .btn { border: none; border-radius: 14px; padding: 12px 18px; font-weight: 700; font-size: 15px; cursor: pointer; }
        .btn-primary { background: linear-gradient(135deg, #2563EB, #60A5FA); color: #fff; width: 100%; }
        .btn-danger { background: #FEF2F2; color: #B91C1C; }
        .ks-subject { font-size: 17px; font-weight: 800; color: #0f172a; margin-bottom: 6px; }
        .ks-when { font-size: 14px; font-weight: 600; color: #2563EB; margin-bottom: 8px; }
        .ks-desc { font-size: 14px; color: #475569; line-height: 1.55; margin-bottom: 10px; white-space: pre-wrap; }
        .ks-meta { font-size: 12px; color: #94A3B8; margin-bottom: 12px; }
        .badge { display: inline-block; padding: 5px 10px; border-radius: 999px; font-size: 11px; font-weight: 700; background: #EFF6FF; color: #2563EB; margin-right: 6px; }
        .badge.ok { background: #ECFDF5; color: #047857; }
        .actions { display: flex; gap: 8px; flex-wrap: wrap; align-items: center; }
        form.inline { display: inline; margin: 0; }
    </style>
</head>
<body class="app-body">
<div class="page-shell">
    <%@ include file="/includes/appTopbar.jspf" %>

    <% if ("1".equals(request.getParameter("created"))) { %>
        <div class="flash ok">Session created.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("joined"))) { %>
        <div class="flash ok">You joined the session.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("left"))) { %>
        <div class="flash ok">You left the session.</div>
    <% } %>
    <% if ("fields".equals(request.getParameter("error"))) { %>
        <div class="flash err">Subject and date/time are required.</div>
    <% } %>
    <% if ("date".equals(request.getParameter("error"))) { %>
        <div class="flash err">Invalid date/time.</div>
    <% } %>
    <% if ("len".equals(request.getParameter("error"))) { %>
        <div class="flash err">Subject is too long.</div>
    <% } %>
    <% if ("join".equals(request.getParameter("error"))) { %>
        <div class="flash err">Could not join (already joined?).</div>
    <% } %>
    <% if ("host".equals(request.getParameter("error"))) { %>
        <div class="flash err">Hosts stay on the attendee list for sessions they run.</div>
    <% } %>
    <% if ("leave".equals(request.getParameter("error"))) { %>
        <div class="flash err">You are not on the attendee list.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("deleted"))) { %>
        <div class="flash ok">Session deleted.</div>
    <% } %>
    <% if ("del".equals(request.getParameter("error"))) { %>
        <div class="flash err">Only the host can delete that session.</div>
    <% } %>

    <h1>Kuppiya</h1>
    <p class="sub">Host a teaching or revision session — others can see upcoming sessions and join.</p>

    <div class="card">
        <h2 style="font-size:16px;margin-bottom:12px;color:#0f172a;">Create session</h2>
        <form action="<%= c %>/kuppiya" method="post">
            <input type="hidden" name="action" value="create"/>
            <label for="subject">Subject</label>
            <input type="text" id="subject" name="subject" maxlength="255" required placeholder="e.g. OOP revision, Database tutorial"/>

            <label for="sessionDateTime">Date &amp; time</label>
            <input type="datetime-local" id="sessionDateTime" name="sessionDateTime" required/>

            <label for="description">Description <span style="font-weight:400;color:#94A3B8;">(optional)</span></label>
            <textarea id="description" name="description" maxlength="4000" placeholder="Topics, room or online link, what to bring…"></textarea>

            <button type="submit" class="btn btn-primary">Publish session</button>
        </form>
    </div>

    <h2 style="font-size:16px;margin:22px 0 12px;color:#0f172a;">Upcoming sessions</h2>
    <% if (kuppiyaSessions == null || kuppiyaSessions.isEmpty()) { %>
        <div class="card" style="text-align:center;color:#64748B;">No upcoming sessions. Create one above.</div>
    <% } else {
        for (KuppiyaSession ks : kuppiyaSessions) {
            User host = ks.getHost();
            String hostName = host != null ? host.getFullName() : "Someone";
            boolean isHost = ks.getHostId() == loggedUser.getUserId();
            String whenStr = ks.getSessionDateTime() != null ? ks.getSessionDateTime().format(fmt) : "";
    %>
        <div class="card">
            <div class="ks-subject"><%= HtmlEscape.escape(ks.getSubject()) %></div>
            <div class="ks-when"><%= HtmlEscape.escape(whenStr) %></div>
            <% if (ks.getDescription() != null && !ks.getDescription().trim().isEmpty()) { %>
                <div class="ks-desc"><%= HtmlEscape.escape(ks.getDescription()) %></div>
            <% } %>
            <div class="ks-meta">
                Teacher (host): <%= HtmlEscape.escape(hostName) %> · <%= ks.getParticipantCount() %> attending
            </div>
            <div class="actions">
                <% if (isHost) { %>
                    <span class="badge ok">Your session</span>
                    <form class="inline" method="post" action="<%= c %>/kuppiya" onsubmit="return confirm('Delete this session for all attendees?');">
                        <input type="hidden" name="action" value="delete"/>
                        <input type="hidden" name="sessionId" value="<%= ks.getSessionId() %>"/>
                        <button type="submit" class="btn btn-danger">Delete session</button>
                    </form>
                <% } %>
                <% if (ks.isJoinedByCurrentUser()) { %>
                    <span class="badge">Attending</span>
                    <% if (!isHost) { %>
                    <form class="inline" method="post" action="<%= c %>/kuppiya">
                        <input type="hidden" name="action" value="leave"/>
                        <input type="hidden" name="sessionId" value="<%= ks.getSessionId() %>"/>
                        <button type="submit" class="btn btn-danger">Leave</button>
                    </form>
                    <% } %>
                <% } else if (!isHost) { %>
                    <form class="inline" method="post" action="<%= c %>/kuppiya">
                        <input type="hidden" name="action" value="join"/>
                        <input type="hidden" name="sessionId" value="<%= ks.getSessionId() %>"/>
                        <button type="submit" class="btn btn-primary" style="width:auto;">Join session</button>
                    </form>
                <% } %>
            </div>
        </div>
    <% } } %>
</div>
<% request.setAttribute("navActive", "kuppiya"); %>
<%@ include file="/includes/bottomNav.jspf" %>
</body>
</html>
