<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>UniBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #F8FAFC, #EAF3FF);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #1E293B;
            padding: 24px;
        }
        .box {
            text-align: center;
            max-width: 480px;
            background: rgba(255, 255, 255, 0.88);
            border: 1px solid rgba(203, 213, 225, 0.75);
            border-radius: 28px;
            padding: 48px 36px;
            box-shadow: 0 18px 60px rgba(37, 99, 235, 0.12);
        }
        .logo-badge {
            width: 72px; height: 72px; margin: 0 auto 18px;
            border-radius: 22px;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            display: flex; align-items: center; justify-content: center;
            color: #fff; font-size: 32px; font-weight: 800;
        }
        h1 { font-size: 44px; font-weight: 800; color: #2563EB; margin-bottom: 10px; }
        p.tag { color: #64748B; font-size: 17px; margin-bottom: 32px; line-height: 1.5; }
        .actions { display: flex; flex-direction: column; gap: 12px; }
        .actions a {
            display: block; padding: 16px 20px; border-radius: 18px; font-size: 16px; font-weight: 700;
            text-decoration: none; text-align: center;
        }
        .primary {
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            color: #fff;
            box-shadow: 0 14px 26px rgba(37, 99, 235, 0.22);
        }
        .secondary {
            background: #fff; color: #2563EB; border: 2px solid #BFDBFE;
        }
        .primary:hover { filter: brightness(1.03); }
        .secondary:hover { background: #EFF6FF; }
    </style>
</head>
<body>
    <div class="box">
        <div class="logo-badge">U</div>
        <h1>UniBook</h1>
        <p class="tag">Your campus network — sign up with your <strong>edu.lk</strong> email or log in.</p>
        <div class="actions">
            <a class="primary" href="<%= request.getContextPath() %>/signup">Sign up</a>
            <a class="secondary" href="<%= request.getContextPath() %>/login.jsp">Log in</a>
        </div>
    </div>
</body>
</html>
