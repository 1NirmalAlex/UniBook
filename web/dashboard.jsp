<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.unibook.model.User"%>
<%@page import="com.unibook.model.Post"%>
<%@page import="com.unibook.util.HtmlEscape"%>
<%@page import="java.util.List"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@ include file="/includes/authCheck.jspf" %>
<%
    if (request.getAttribute("timelinePosts") == null) {
        response.sendRedirect(request.getContextPath() + "/feed");
        return;
    }
    @SuppressWarnings("unchecked")
    List<Post> timelinePosts = (List<Post>) request.getAttribute("timelinePosts");
    DateTimeFormatter postTimeFmt = DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a");
%>
<!DOCTYPE html>
<html>
<head>
    <title>UniBook - Events &amp; Posts</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/app.css"/>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        .visually-hidden {
            position: absolute;
            width: 1px;
            height: 1px;
            padding: 0;
            margin: -1px;
            overflow: hidden;
            clip: rect(0, 0, 0, 0);
            white-space: nowrap;
            border: 0;
        }

        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #F8FAFC, #EAF3FF);
            min-height: 100vh;
            color: #1E293B;
            overflow-x: hidden;
            position: relative;
        }

        body::before,
        body::after {
            content: "";
            position: fixed;
            width: 360px;
            height: 360px;
            border-radius: 50%;
            z-index: 0;
            filter: blur(70px);
            opacity: 0.35;
            animation: bgFloat 11s ease-in-out infinite;
        }

        body::before {
            background: #60A5FA;
            top: -100px;
            left: -100px;
        }

        body::after {
            background: #BFDBFE;
            bottom: -100px;
            right: -100px;
            animation-delay: 3s;
        }

        @keyframes bgFloat {
            0%, 100% {
                transform: translate(0, 0) scale(1);
            }
            50% {
                transform: translate(25px, 30px) scale(1.08);
            }
        }

        .page {
            position: relative;
            z-index: 1;
            max-width: 520px;
            margin: 0 auto;
            padding: 18px 16px 25px;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 18px;
        }

        .brand {
            font-size: 34px;
            font-weight: 800;
            color: #2563EB;
            letter-spacing: 0.5px;
            text-shadow: 0 6px 18px rgba(37, 99, 235, 0.12);
        }

        .brand span {
            color: #1E293B;
        }

        .top-icons {
            display: flex;
            gap: 10px;
        }

        .icon-btn {
            width: 42px;
            height: 42px;
            border-radius: 14px;
            border: 1px solid #DBEAFE;
            background: rgba(255, 255, 255, 0.86);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #2563EB;
            font-size: 18px;
            cursor: pointer;
            box-shadow: 0 8px 20px rgba(37, 99, 235, 0.10);
            transition: transform 0.25s ease, box-shadow 0.25s ease, background 0.25s ease;
        }

        .icon-btn:hover {
            transform: translateY(-3px) scale(1.06);
            box-shadow: 0 12px 24px rgba(37, 99, 235, 0.18);
            background: #EFF6FF;
        }

        .glass-card {
            background: rgba(255, 255, 255, 0.85);
            backdrop-filter: blur(14px);
            border: 1px solid rgba(203, 213, 225, 0.75);
            border-radius: 24px;
            box-shadow: 0 18px 40px rgba(37, 99, 235, 0.10);
        }

        .composer {
            padding: 18px;
            margin-bottom: 18px;
            position: relative;
            overflow: hidden;
        }

        .composer::before {
            content: "";
            position: absolute;
            width: 130px;
            height: 130px;
            border-radius: 50%;
            background: #60A5FA;
            opacity: 0.10;
            top: -30px;
            right: -30px;
            animation: blobMove 7s ease-in-out infinite;
        }

        @keyframes blobMove {
            0%, 100% {
                transform: translate(0, 0) scale(1);
            }
            50% {
                transform: translate(10px, 10px) scale(1.1);
            }
        }

        .composer-top {
            display: flex;
            gap: 12px;
            align-items: center;
            margin-bottom: 14px;
            position: relative;
            z-index: 1;
        }

        .avatar {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            flex-shrink: 0;
            box-shadow: 0 8px 18px rgba(37, 99, 235, 0.18);
        }

        .composer-form {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        .composer-form textarea {
            width: 100%;
            min-height: 72px;
            border: 1px solid #BFDBFE;
            background: #F8FAFC;
            border-radius: 18px;
            padding: 14px 16px;
            font-size: 15px;
            color: #1E293B;
            font-family: inherit;
            resize: vertical;
        }
        .composer-form textarea:focus {
            outline: none;
            border-color: #60A5FA;
            box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2);
        }
        .composer-submit {
            align-self: flex-end;
            border: none;
            border-radius: 14px;
            padding: 10px 20px;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            color: #fff;
            font-weight: 700;
            font-size: 14px;
            cursor: pointer;
        }
        .post-delete-btn {
            margin-top: 10px;
            border: 1px solid #FECACA;
            background: #FEF2F2;
            color: #B91C1C;
            border-radius: 12px;
            padding: 8px 14px;
            font-size: 13px;
            font-weight: 700;
            cursor: pointer;
        }
        .post-delete-btn:hover {
            background: #FEE2E2;
        }

        .composer-actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            position: relative;
            z-index: 1;
        }

        .mini-action {
            border: none;
            background: #EFF6FF;
            color: #2563EB;
            padding: 10px 14px;
            border-radius: 14px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.25s ease, background 0.25s ease;
        }

        .mini-action:hover {
            transform: translateY(-2px);
            background: #DBEAFE;
        }

        .stories-section {
            display: flex;
            gap: 14px;
            overflow-x: auto;
            padding: 8px 2px 16px;
            margin-bottom: 8px;
            scrollbar-width: none;
        }

        .stories-section::-webkit-scrollbar {
            display: none;
        }

        .story-card {
            min-width: 82px;
            text-align: center;
            flex-shrink: 0;
        }

        .story-circle {
            width: 70px;
            height: 70px;
            border-radius: 50%;
            margin: 0 auto 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 3px solid #93C5FD;
            background: linear-gradient(135deg, #FFFFFF, #DBEAFE);
            box-shadow: 0 10px 22px rgba(37, 99, 235, 0.10);
            color: #2563EB;
            font-size: 20px;
            transition: transform 0.25s ease;
        }

        .story-card:hover .story-circle {
            transform: scale(1.08);
        }

        .story-name {
            font-size: 13px;
            color: #475569;
            font-weight: 600;
        }

        .feed {
            display: grid;
            gap: 18px;
        }

        .post-card {
            padding: 18px;
            overflow: hidden;
            position: relative;
        }

        .post-card::after {
            content: "";
            position: absolute;
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: #BFDBFE;
            opacity: 0.08;
            right: -20px;
            bottom: -25px;
        }

        .post-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 14px;
        }

        .post-avatar {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 700;
            font-size: 16px;
            flex-shrink: 0;
        }

        .post-meta {
            flex: 1;
        }

        .post-name {
            font-size: 17px;
            font-weight: 700;
            color: #1E293B;
        }

        .post-time {
            font-size: 12px;
            color: #64748B;
            margin-top: 2px;
        }

        .post-tag {
            display: inline-block;
            margin-top: 5px;
            padding: 5px 10px;
            border-radius: 999px;
            background: #EFF6FF;
            color: #2563EB;
            font-size: 11px;
            font-weight: 700;
        }

        .post-text {
            font-size: 14px;
            line-height: 1.8;
            color: #475569;
            margin-bottom: 15px;
        }

        .post-image {
            height: 220px;
            border-radius: 18px;
            background: linear-gradient(135deg, #BFDBFE, #60A5FA);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 26px;
            font-weight: 700;
            margin-bottom: 16px;
            box-shadow: inset 0 0 30px rgba(255,255,255,0.12);
        }

        .post-actions {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 10px;
        }

        .post-action-btn {
            border: none;
            background: #F8FAFC;
            color: #2563EB;
            border-radius: 14px;
            padding: 12px 10px;
            font-weight: 700;
            font-size: 13px;
            cursor: pointer;
            border: 1px solid #DBEAFE;
            transition: transform 0.25s ease, background 0.25s ease, color 0.25s ease;
        }

        .post-action-btn:hover {
            transform: translateY(-2px);
            background: #EFF6FF;
            color: #1D4ED8;
        }

        .bottom-nav {
            position: fixed;
            left: 50%;
            bottom: 14px;
            transform: translateX(-50%);
            width: calc(100% - 24px);
            max-width: 500px;
            z-index: 10;
            background: rgba(255, 255, 255, 0.88);
            backdrop-filter: blur(14px);
            border: 1px solid rgba(203, 213, 225, 0.8);
            border-radius: 24px;
            box-shadow: 0 16px 36px rgba(37, 99, 235, 0.12);
            padding: 10px 8px;
        }

        .bottom-nav-inner {
            display: grid;
            grid-template-columns: repeat(5, 1fr);
            gap: 8px;
        }

        .nav-item {
            border: none;
            background: transparent;
            border-radius: 16px;
            padding: 10px 4px;
            color: #64748B;
            cursor: pointer;
            transition: background 0.25s ease, transform 0.25s ease, color 0.25s ease;
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 6px;
            font-size: 12px;
            font-weight: 700;
        }

        .nav-item:hover,
        .nav-item.active {
            background: #EFF6FF;
            color: #2563EB;
            transform: translateY(-2px);
        }

        .nav-icon {
            font-size: 18px;
        }

        @media (max-width: 560px) {
            .brand {
                font-size: 29px;
            }

            .page {
                padding: 16px 12px 25px;
            }

            .post-image {
                height: 190px;
            }

            .post-actions {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body class="app-body">
    <div class="page">

        <%@ include file="/includes/appTopbar.jspf" %>

        <% if ("1".equals(request.getParameter("posted"))) { %>
            <div class="flash ok" style="max-width:520px;margin:0 auto 14px;padding:0 16px;">Post published.</div>
        <% } %>
        <% if ("empty".equals(request.getParameter("error"))) { %>
            <div class="flash err" style="max-width:520px;margin:0 auto 14px;padding:0 16px;">Write something before posting.</div>
        <% } %>
        <% if ("len".equals(request.getParameter("error"))) { %>
            <div class="flash err" style="max-width:520px;margin:0 auto 14px;padding:0 16px;">Post is too long (max 8,000 characters).</div>
        <% } %>
        <% if ("1".equals(request.getParameter("deleted"))) { %>
            <div class="flash ok" style="max-width:520px;margin:0 auto 14px;padding:0 16px;">Post deleted.</div>
        <% } %>
        <% if ("delpost".equals(request.getParameter("error"))) { %>
            <div class="flash err" style="max-width:520px;margin:0 auto 14px;padding:0 16px;">You can only delete your own posts.</div>
        <% } %>

        <div class="composer glass-card">
            <form action="<%= request.getContextPath() %>/feed" method="post">
                <div class="composer-top">
                    <div class="avatar"><%= loggedUser.getFirstName() != null && !loggedUser.getFirstName().isEmpty()
                            ? loggedUser.getFirstName().substring(0, 1).toUpperCase() : "U" %></div>
                    <div class="composer-form">
                        <label for="postContent" class="visually-hidden">Post content</label>
                        <textarea id="postContent" name="content" maxlength="8000" required placeholder="What's happening in your university today?"></textarea>
                        <button type="submit" class="composer-submit">Post</button>
                    </div>
                </div>
            </form>
        </div>

        <p style="text-align:center;font-size:13px;color:#64748B;margin:8px 0 14px;">Showing your posts and posts from friends</p>

        <div class="feed">
            <% if (timelinePosts == null || timelinePosts.isEmpty()) { %>
                <div class="glass-card" style="padding:24px;text-align:center;color:#64748B;font-size:15px;">
                    No posts yet. Add friends to see their updates, or write the first post.
                </div>
            <% } else {
                for (Post post : timelinePosts) {
                    User author = post.getAuthor();
                    String initials = "?";
                    if (author != null && author.getFirstName() != null && !author.getFirstName().isEmpty()) {
                        initials = author.getFirstName().substring(0, 1).toUpperCase();
                    }
                    String authorName = author != null ? author.getFullName() : "User";
                    String timeStr = post.getCreatedAt() != null ? post.getCreatedAt().format(postTimeFmt) : "";
            %>
            <div class="post-card glass-card">
                <div class="post-header">
                    <% if (author != null && author.getProfilePicturePath() != null && !author.getProfilePicturePath().isEmpty()) { %>
                        <div class="post-avatar" style="padding:0;overflow:hidden;background:none;">
                            <img src="<%= request.getContextPath() %>/<%= author.getProfilePicturePath() %>" alt="" style="width:100%;height:100%;object-fit:cover;"/>
                        </div>
                    <% } else { %>
                        <div class="post-avatar"><%= initials %></div>
                    <% } %>
                    <div class="post-meta">
                        <div class="post-name"><%= HtmlEscape.escape(authorName) %></div>
                        <div class="post-time"><%= HtmlEscape.escape(timeStr) %></div>
                        <% if (post.getAuthorId() == loggedUser.getUserId()) { %>
                            <span class="post-tag">You</span>
                        <% } else { %>
                            <span class="post-tag">Friend</span>
                        <% } %>
                    </div>
                </div>
                <div class="post-text"><%= HtmlEscape.escape(post.getContent()).replace("\n", "<br/>") %></div>
                <% if (post.getAuthorId() == loggedUser.getUserId()) { %>
                <form method="post" action="<%= request.getContextPath() %>/feed" onsubmit="return confirm('Delete this post permanently?');">
                    <input type="hidden" name="action" value="delete"/>
                    <input type="hidden" name="postId" value="<%= post.getPostId() %>"/>
                    <button type="submit" class="post-delete-btn">Delete post</button>
                </form>
                <% } %>
            </div>
            <% } } %>
        </div>
    </div>

    <% request.setAttribute("navActive", "feed"); %>
    <%@ include file="/includes/bottomNav.jspf" %>
</body>
</html>