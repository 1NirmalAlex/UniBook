<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.unibook.util.HtmlEscape"%>
<%
    String loginNext = request.getParameter("next");
    if (loginNext == null) {
        loginNext = (String) request.getAttribute("loginNext");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>User Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #F8FAFC, #EAF3FF);
            min-height: 100vh;
            overflow-x: hidden;
            position: relative;
            color: #1E293B;
        }

        body::before,
        body::after {
            content: "";
            position: fixed;
            width: 420px;
            height: 420px;
            border-radius: 50%;
            z-index: 0;
            filter: blur(65px);
            opacity: 0.42;
            animation: floatBg 12s ease-in-out infinite;
        }

        body::before {
            background: #60A5FA;
            top: -130px;
            left: -130px;
        }

        body::after {
            background: #BFDBFE;
            bottom: -140px;
            right: -120px;
            animation-delay: 3s;
        }

        @keyframes floatBg {
            0% {
                transform: translate(0, 0) scale(1);
            }
            50% {
                transform: translate(24px, 30px) scale(1.08);
            }
            100% {
                transform: translate(0, 0) scale(1);
            }
        }

        .page-wrapper {
            position: relative;
            z-index: 1;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 18px;
        }

        .login-card {
            position: relative;
            width: 100%;
            max-width: 560px;
            background: rgba(255, 255, 255, 0.84);
            backdrop-filter: blur(14px);
            border: 1px solid rgba(203, 213, 225, 0.75);
            border-radius: 28px;
            box-shadow: 0 18px 60px rgba(37, 99, 235, 0.14);
            padding: 38px 32px 34px;
            overflow: hidden;
        }

        .login-card::before,
        .login-card::after {
            content: "";
            position: absolute;
            border-radius: 50%;
            z-index: 0;
            opacity: 0.16;
            animation: cardBlob 7s ease-in-out infinite;
        }

        .login-card::before {
            width: 170px;
            height: 170px;
            background: #60A5FA;
            top: -45px;
            right: -35px;
        }

        .login-card::after {
            width: 115px;
            height: 115px;
            background: #2563EB;
            bottom: -28px;
            left: -22px;
            animation-delay: 2s;
        }

        @keyframes cardBlob {
            0%, 100% {
                transform: translate(0, 0) scale(1);
            }
            50% {
                transform: translate(10px, 12px) scale(1.1);
            }
        }

        .content {
            position: relative;
            z-index: 2;
        }

        .brand-row {
            text-align: center;
            margin-bottom: 24px;
        }

        .logo-badge {
            width: 70px;
            height: 70px;
            margin: 0 auto 16px;
            border-radius: 22px;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 10px 24px rgba(37, 99, 235, 0.26);
            animation: pulseLogo 3s ease-in-out infinite;
        }

        .logo-badge span {
            color: #FFFFFF;
            font-size: 30px;
            font-weight: 800;
            letter-spacing: 1px;
        }

        @keyframes pulseLogo {
            0%, 100% {
                transform: translateY(0) scale(1);
            }
            50% {
                transform: translateY(-4px) scale(1.04);
            }
        }

        .page-title {
            font-size: 40px;
            font-weight: 800;
            color: #2563EB;
            letter-spacing: 0.5px;
            margin-bottom: 10px;
            text-shadow: 0 6px 18px rgba(37, 99, 235, 0.12);
        }

        .page-subtitle {
            font-size: 16px;
            color: #64748B;
            line-height: 1.7;
            max-width: 420px;
            margin: 0 auto;
        }

        .error {
            margin: 22px 0 10px;
            padding: 13px 15px;
            border-radius: 14px;
            background: #FEF2F2;
            border: 1px solid #FECACA;
            color: #DC2626;
            text-align: center;
            font-size: 14px;
            font-weight: 600;
        }

        form {
            margin-top: 26px;
        }

        .form-grid {
            display: grid;
            gap: 18px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        label {
            font-size: 14px;
            font-weight: 700;
            color: #1E293B;
            padding-left: 2px;
        }

        input {
            width: 100%;
            padding: 15px 16px;
            border: 1px solid #CBD5E1;
            border-radius: 16px;
            background: rgba(255, 255, 255, 0.92);
            color: #1E293B;
            font-size: 15px;
            outline: none;
            transition: border-color 0.25s ease, box-shadow 0.25s ease, transform 0.25s ease;
        }

        input::placeholder {
            color: #94A3B8;
        }

        input:focus {
            border-color: #60A5FA;
            box-shadow: 0 0 0 4px rgba(96, 165, 250, 0.18);
            transform: translateY(-1px);
        }

        .helper-text {
            font-size: 12px;
            color: #64748B;
            padding-left: 2px;
            line-height: 1.5;
        }

        .button-row {
            margin-top: 8px;
        }

        button {
            width: 100%;
            border: none;
            border-radius: 18px;
            padding: 16px 18px;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            color: #FFFFFF;
            font-size: 16px;
            font-weight: 700;
            cursor: pointer;
            box-shadow: 0 14px 26px rgba(37, 99, 235, 0.22);
            transition: transform 0.3s ease, box-shadow 0.3s ease, filter 0.3s ease;
        }

        button:hover {
            transform: translateY(-3px) scale(1.01);
            box-shadow: 0 18px 32px rgba(37, 99, 235, 0.28);
            filter: brightness(1.03);
        }

        .footer-note {
            margin-top: 18px;
            text-align: center;
            font-size: 13px;
            color: #64748B;
        }

        @media (max-width: 680px) {
            .login-card {
                padding: 30px 20px 26px;
                border-radius: 24px;
            }

            .page-title {
                font-size: 32px;
            }

            .page-subtitle {
                font-size: 15px;
            }

            input {
                padding: 14px 14px;
            }
        }
    </style>
</head>
<body>
    <div class="page-wrapper">
        <div class="login-card">
            <div class="content">
                <div class="brand-row">
                    <div class="logo-badge">
                        <span>U</span>
                    </div>
                    <h2 class="page-title">User Login</h2>
                    <p class="page-subtitle">
                        Enter your account details to access the UniBook platform
                        safely and continue your journey.
                    </p>
                </div>

                <%
                    String errorMessage = (String) request.getAttribute("errorMessage");
                    if (errorMessage != null) {
                %>
                    <p class="error"><%= errorMessage %></p>
                <%
                    }
                    if ("1".equals(request.getParameter("registered"))) {
                %>
                    <p style="margin: 22px 0 10px; padding: 13px 15px; border-radius: 14px; background: #ECFDF5; border: 1px solid #A7F3D0; color: #047857; text-align: center; font-size: 14px; font-weight: 600;">Registration successful. You can log in now.</p>
                <%
                    }
                %>

                <form action="<%= request.getContextPath() %>/loginUser" method="post">
                    <% if (loginNext != null && !loginNext.trim().isEmpty()) { %>
                        <input type="hidden" name="next" value="<%= HtmlEscape.escape(loginNext.trim()) %>"/>
                    <% } %>
                    <div class="form-grid">
                        <div class="form-group">
                            <label for="email">Email Address</label>
                            <input type="email" id="email" name="email" placeholder="Enter your email address" />
                            <span class="helper-text">Use the email linked with your UniBook account</span>
                        </div>

                        <div class="form-group">
                            <label for="password">Password</label>
                            <input type="password" id="password" name="password" placeholder="Enter your password" />
                            <span class="helper-text">Make sure your password is typed correctly</span>
                        </div>
                    </div>

                    <div class="button-row">
                        <button type="submit">Login</button>
                    </div>
                </form>

                <p class="footer-note"><a href="<%= request.getContextPath() %>/signup.jsp" style="color:#2563EB;font-weight:600;text-decoration:none;">Create an account</a> · UniBook Student Platform</p>
            </div>
        </div>
    </div>
</body>
</html>