<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>UniBook - Sign Up</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #F8FAFC, #EAF3FF);
            min-height: 100vh;
            color: #1E293B;
        }
        .page-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 32px 16px;
        }
        .card {
            width: 100%;
            max-width: 520px;
            background: rgba(255, 255, 255, 0.9);
            border: 1px solid rgba(203, 213, 225, 0.75);
            border-radius: 28px;
            box-shadow: 0 18px 60px rgba(37, 99, 235, 0.12);
            padding: 36px 28px;
        }
        .brand { text-align: center; margin-bottom: 22px; }
        .logo-badge {
            width: 64px; height: 64px; margin: 0 auto 12px;
            border-radius: 20px;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            display: flex; align-items: center; justify-content: center;
            color: #fff; font-size: 28px; font-weight: 800;
        }
        h1 { font-size: 28px; color: #2563EB; margin-bottom: 8px; }
        .sub { color: #64748B; font-size: 15px; margin-bottom: 20px; }
        .error {
            padding: 12px 14px; border-radius: 14px;
            background: #FEF2F2; border: 1px solid #FECACA;
            color: #DC2626; font-size: 14px; font-weight: 600; margin-bottom: 18px;
        }
        label { display: block; font-size: 13px; font-weight: 700; color: #1E293B; margin-bottom: 6px; }
        input {
            width: 100%; padding: 12px 14px; border: 1px solid #CBD5E1; border-radius: 14px;
            font-size: 15px; margin-bottom: 14px;
        }
        input:focus {
            outline: none; border-color: #60A5FA;
            box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2);
        }
        button[type="submit"] {
            width: 100%; border: none; border-radius: 16px; padding: 14px;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            color: #fff; font-size: 16px; font-weight: 700; cursor: pointer;
            margin-top: 6px;
        }
        .footer { text-align: center; margin-top: 18px; font-size: 14px; color: #64748B; }
        .footer a { color: #2563EB; font-weight: 600; text-decoration: none; }
        .footer a:hover { text-decoration: underline; }
        .hint { font-size: 12px; color: #64748B; margin-top: -10px; margin-bottom: 12px; }
    </style>
</head>
<body>
    <div class="page-wrapper">
        <div class="card">
            <div class="brand">
                <div class="logo-badge">U</div>
                <h1>Create account</h1>
                <p class="sub">Join UniBook with your campus email</p>
            </div>

            <%
                String err = (String) request.getAttribute("errorMessage");
                if (err != null) {
            %>
                <p class="error"><%= err %></p>
            <% } %>

            <form action="<%= request.getContextPath() %>/signup" method="post">
                <label for="firstName">First name</label>
                <input type="text" id="firstName" name="firstName" required
                       value="${not empty firstName ? firstName : ''}" autocomplete="given-name" />

                <label for="lastName">Last name</label>
                <input type="text" id="lastName" name="lastName" required
                       value="${not empty lastName ? lastName : ''}" autocomplete="family-name" />

                <label for="universityCampus">University / Campus</label>
                <input type="text" id="universityCampus" name="universityCampus" required
                       value="${not empty universityCampus ? universityCampus : ''}" />

                <label for="degreeProgram">Degree program</label>
                <input type="text" id="degreeProgram" name="degreeProgram" required
                       value="${not empty degreeProgram ? degreeProgram : ''}" />

                <label for="email">Campus email</label>
                <input type="email" id="email" name="email" required placeholder="you@university.edu.lk"
                       value="${not empty email ? email : ''}" autocomplete="email" />
                <p class="hint">Must end with <strong>edu.lk</strong></p>

                <label for="password">Password</label>
                <input type="password" id="password" name="password" required autocomplete="new-password" />

                <label for="confirmPassword">Confirm password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required autocomplete="new-password" />

                <button type="submit">Sign up</button>
            </form>

            <p class="footer">Already have an account? <a href="<%= request.getContextPath() %>/login.jsp">Log in</a></p>
        </div>
    </div>
</body>
</html>
