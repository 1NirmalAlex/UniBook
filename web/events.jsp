<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.unibook.model.User"%>
<%@page import="com.unibook.model.Event"%>
<%@page import="com.unibook.util.HtmlEscape"%>
<%@page import="java.util.List"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@ include file="/includes/authCheck.jspf" %>
<%
    if (request.getAttribute("upcomingEvents") == null) {
        response.sendRedirect(request.getContextPath() + "/events");
        return;
    }
    @SuppressWarnings("unchecked")
    List<Event> upcomingEvents = (List<Event>) request.getAttribute("upcomingEvents");
    DateTimeFormatter evFmt = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy 'at' h:mm a");
    String c = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Events - UniBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="stylesheet" href="<%= c %>/css/app.css"/>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: "Segoe UI", Tahoma, sans-serif; background: linear-gradient(135deg, #F8FAFC, #EAF3FF); min-height: 100vh; color: #1E293B; }
        .page-shell { max-width: 520px; margin: 0 auto; padding: 18px 16px; }
        h1 { font-size: 22px; color: #2563EB; margin-bottom: 6px; }
        .sub { color: #64748B; font-size: 14px; margin-bottom: 18px; }
        .card { background: rgba(255, 255, 255, 0.9); border: 1px solid #e2e8f0; border-radius: 20px; padding: 18px; margin-bottom: 14px; box-shadow: 0 8px 28px rgba(37, 99, 235, 0.06); }
        label { display: block; font-size: 13px; font-weight: 700; margin-bottom: 6px; color: #1E293B; }
        input[type="text"], input[type="datetime-local"], textarea {
            width: 100%; padding: 12px 14px; border: 1px solid #CBD5E1; border-radius: 14px; font-size: 15px; margin-bottom: 12px; font-family: inherit;
        }
        textarea { min-height: 80px; resize: vertical; }
        .btn { border: none; border-radius: 14px; padding: 12px 18px; font-weight: 700; font-size: 15px; cursor: pointer; }
        .btn-primary { background: linear-gradient(135deg, #2563EB, #60A5FA); color: #fff; width: 100%; }
        .btn-ghost { background: #F1F5F9; color: #475569; width: auto; }
        .btn-danger { background: #FEF2F2; color: #B91C1C; }
        .ev-title { font-size: 17px; font-weight: 800; color: #0f172a; margin-bottom: 6px; }
        .ev-when { font-size: 14px; font-weight: 600; color: #2563EB; margin-bottom: 6px; }
        .ev-loc { font-size: 13px; color: #64748B; margin-bottom: 8px; }
        .ev-desc { font-size: 14px; color: #475569; line-height: 1.55; margin-bottom: 10px; white-space: pre-wrap; }
        .ev-meta { font-size: 12px; color: #94A3B8; margin-bottom: 12px; }
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
        <div class="flash ok">Event created.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("joined"))) { %>
        <div class="flash ok">You joined the event.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("left"))) { %>
        <div class="flash ok">You left the event.</div>
    <% } %>
    <% if ("fields".equals(request.getParameter("error"))) { %>
        <div class="flash err">Title and date/time are required.</div>
    <% } %>
    <% if ("date".equals(request.getParameter("error"))) { %>
        <div class="flash err">Invalid date/time.</div>
    <% } %>
    <% if ("len".equals(request.getParameter("error"))) { %>
        <div class="flash err">Title or location is too long.</div>
    <% } %>
    <% if ("join".equals(request.getParameter("error"))) { %>
        <div class="flash err">Could not join (already joined?).</div>
    <% } %>
    <% if ("host".equals(request.getParameter("error"))) { %>
        <div class="flash err">Hosts stay on the attendee list for their own events.</div>
    <% } %>
    <% if ("leave".equals(request.getParameter("error"))) { %>
        <div class="flash err">You are not on the attendee list.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("deleted"))) { %>
        <div class="flash ok">Event deleted.</div>
    <% } %>
    <% if ("del".equals(request.getParameter("error"))) { %>
        <div class="flash err">Only the host can delete that event.</div>
    <% } %>

    <h1>Events</h1>
    <p class="sub">Create campus events and see who’s going</p>

    <div class="card">
        <h2 style="font-size:16px;margin-bottom:12px;color:#0f172a;">Create event</h2>
        <form action="<%= c %>/events" method="post">
            <input type="hidden" name="action" value="create"/>
            <label for="title">Title</label>
            <input type="text" id="title" name="title" maxlength="255" required placeholder="Workshop, meet-up, sports…"/>

            <label for="eventDateTime">Date &amp; time</label>
            <input type="datetime-local" id="eventDateTime" name="eventDateTime" required/>

            <label for="location">Location <span style="font-weight:400;color:#94A3B8;">(optional)</span></label>
            <input type="text" id="location" name="location" maxlength="255" placeholder="Building, room, or online link"/>

            <label for="description">Description <span style="font-weight:400;color:#94A3B8;">(optional)</span></label>
            <textarea id="description" name="description" maxlength="4000" placeholder="What should people know?"></textarea>

            <button type="submit" class="btn btn-primary">Publish event</button>
        </form>
    </div>

    <h2 style="font-size:16px;margin:22px 0 12px;color:#0f172a;">Upcoming</h2>
    <% if (upcomingEvents == null || upcomingEvents.isEmpty()) { %>
        <div class="card" style="text-align:center;color:#64748B;">No upcoming events. Create one above.</div>
    <% } else {
        for (Event ev : upcomingEvents) {
            User host = ev.getCreator();
            String hostName = host != null ? host.getFullName() : "Someone";
            boolean isHost = ev.getCreatorId() == loggedUser.getUserId();
            String whenStr = ev.getEventDateTime() != null ? ev.getEventDateTime().format(evFmt) : "";
    %>
        <div class="card">
            <div class="ev-title"><%= HtmlEscape.escape(ev.getTitle()) %></div>
            <div class="ev-when"><%= HtmlEscape.escape(whenStr) %></div>
            <% if (ev.getLocation() != null && !ev.getLocation().trim().isEmpty()) { %>
                <div class="ev-loc">📍 <%= HtmlEscape.escape(ev.getLocation()) %></div>
            <% } %>
            <% if (ev.getDescription() != null && !ev.getDescription().trim().isEmpty()) { %>
                <div class="ev-desc"><%= HtmlEscape.escape(ev.getDescription()) %></div>
            <% } %>
            <div class="ev-meta">
                Host: <%= HtmlEscape.escape(hostName) %> · <%= ev.getParticipantCount() %> going
            </div>
            <div class="actions">
                <% if (isHost) { %>
                    <span class="badge ok">Your event</span>
                    <form class="inline" method="post" action="<%= c %>/events" onsubmit="return confirm('Delete this event for everyone? This cannot be undone.');">
                        <input type="hidden" name="action" value="delete"/>
                        <input type="hidden" name="eventId" value="<%= ev.getEventId() %>"/>
                        <button type="submit" class="btn btn-danger">Delete event</button>
                    </form>
                <% } %>
                <% if (ev.isJoinedByCurrentUser()) { %>
                    <span class="badge">Attending</span>
                    <% if (!isHost) { %>
                    <form class="inline" method="post" action="<%= c %>/events">
                        <input type="hidden" name="action" value="leave"/>
                        <input type="hidden" name="eventId" value="<%= ev.getEventId() %>"/>
                        <button type="submit" class="btn btn-danger">Leave</button>
                    </form>
                    <% } %>
                <% } else if (!isHost) { %>
                    <form class="inline" method="post" action="<%= c %>/events">
                        <input type="hidden" name="action" value="join"/>
                        <input type="hidden" name="eventId" value="<%= ev.getEventId() %>"/>
                        <button type="submit" class="btn btn-primary" style="width:auto;">Join</button>
                    </form>
                <% } %>
            </div>
        </div>
    <% } } %>
</div>
<% request.setAttribute("navActive", "events"); %>
<%@ include file="/includes/bottomNav.jspf" %>
</body>
</html>
