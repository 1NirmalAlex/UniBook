 <%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Campus Registration</title>
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

        .register-card {
            position: relative;
            width: 100%;
            max-width: 620px;
            background: rgba(255, 255, 255, 0.84);
            backdrop-filter: blur(14px);
            border: 1px solid rgba(203, 213, 225, 0.75);
            border-radius: 28px;
            box-shadow: 0 18px 60px rgba(37, 99, 235, 0.14);
            padding: 38px 32px 34px;
            overflow: hidden;
        }

        .register-card::before,
        .register-card::after {
            content: "";
            position: absolute;
            border-radius: 50%;
            z-index: 0;
            opacity: 0.16;
            animation: cardBlob 7s ease-in-out infinite;
        }

        .register-card::before {
            width: 170px;
            height: 170px;
            background: #60A5FA;
            top: -45px;
            right: -35px;
        }

        .register-card::after {
            width: 115px;
            height: 115px;
            background: #22C55E;
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
            max-width: 460px;
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
            .register-card {
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
        <div class="register-card">
            <div class="content">
                <div class="brand-row">
                    <div class="logo-badge">
                        <span>U</span>
                    </div>
                    <h2 class="page-title">Campus Registration</h2>
                    <p class="page-subtitle">
                        Register your campus details to continue into the UniBook platform.
                        Fill in the information below clearly and correctly.
                    </p>
                </div>

                <%
                    String errorMessage = (String) request.getAttribute("errorMessage");
                    if (errorMessage != null) {
                %>
                    <p class="error"><%= errorMessage %></p>
                <%
                    }
                %>

                <form action="registerCampus" method="post">
                    <div class="form-grid">
                        <div class="form-group">
                            <label for="campusName">Campus Name</label>
                            <input type="text" id="campusName" name="campusName" placeholder="Enter your campus name" />
                            <span class="helper-text">Example: Horizon Campus, NSBM, SLIIT</span>
                        </div>

                        <div class="form-group">
                            <label for="campusCode">Campus Code</label>
                            <input type="text" id="campusCode" name="campusCode" placeholder="Enter your campus code" />
                            <span class="helper-text">Use the official code or short campus identifier</span>
                        </div>

                        <div class="form-group">
                            <label for="location">Location</label>
                            <input type="text" id="location" name="location" placeholder="Enter campus location" />
                            <span class="helper-text">City, area, or main campus location</span>
                        </div>
                    </div>

                    <div class="button-row">
                        <button type="submit">Register Campus</button>
                    </div>
                </form>

                <p class="footer-note">UniBook Student Platform • Clean • Smart • Modern</p>
            </div>
        </div>
    </div>
</body>
</html>