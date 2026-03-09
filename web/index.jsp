<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>UniBook - Entry Page</title>
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

        /* Animated background */
        body::before,
        body::after {
            content: "";
            position: fixed;
            width: 420px;
            height: 420px;
            border-radius: 50%;
            z-index: 0;
            filter: blur(60px);
            opacity: 0.45;
            animation: floatBg 12s ease-in-out infinite;
        }

        body::before {
            background: #60A5FA;
            top: -120px;
            left: -120px;
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
                transform: translate(25px, 35px) scale(1.08);
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
            padding: 40px 20px;
        }

        .main-box {
            width: 100%;
            max-width: 1180px;
            background: rgba(255, 255, 255, 0.82);
            backdrop-filter: blur(14px);
            border: 1px solid rgba(203, 213, 225, 0.7);
            border-radius: 28px;
            box-shadow: 0 18px 60px rgba(37, 99, 235, 0.12);
            padding: 45px 35px 40px;
            text-align: center;
        }

        .logo-area {
            margin-bottom: 18px;
        }

        .logo-badge {
            width: 72px;
            height: 72px;
            margin: 0 auto 16px;
            border-radius: 22px;
            background: linear-gradient(135deg, #2563EB, #60A5FA);
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 10px 24px rgba(37, 99, 235, 0.28);
            animation: pulseLogo 3s ease-in-out infinite;
        }

        .logo-badge span {
            color: white;
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

        .brand-title {
            font-size: 56px;
            font-weight: 800;
            letter-spacing: 1px;
            color: #2563EB;
            text-shadow: 0 6px 20px rgba(37, 99, 235, 0.15);
            margin-bottom: 10px;
        }

        .subtitle {
            font-size: 18px;
            color: #64748B;
            margin-bottom: 36px;
        }

        .cards-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 22px;
        }

        .user-card {
            position: relative;
            overflow: hidden;
            min-height: 250px;
            border: none;
            border-radius: 24px;
            padding: 28px 22px;
            text-align: left;
            cursor: pointer;
            background: linear-gradient(180deg, #FFFFFF, #F8FAFC);
            border: 1px solid #DBEAFE;
            box-shadow: 0 10px 25px rgba(15, 23, 42, 0.08);
            transition: transform 0.35s ease, box-shadow 0.35s ease, border-color 0.35s ease;
        }

        .user-card:hover {
            transform: translateY(-8px) scale(1.04);
            box-shadow: 0 18px 38px rgba(37, 99, 235, 0.18);
            border-color: #60A5FA;
        }

        .user-card::before {
            content: "";
            position: absolute;
            width: 140px;
            height: 140px;
            border-radius: 50%;
            top: -35px;
            right: -35px;
            opacity: 0.22;
            animation: blobMove 6s ease-in-out infinite;
        }

        .user-card::after {
            content: "";
            position: absolute;
            width: 90px;
            height: 90px;
            border-radius: 50%;
            bottom: -20px;
            left: -20px;
            opacity: 0.18;
            animation: blobMove 7s ease-in-out infinite reverse;
        }

        .card-one::before,
        .card-one::after {
            background: #60A5FA;
        }

        .card-two::before,
        .card-two::after {
            background: #22C55E;
        }

        .card-three::before,
        .card-three::after {
            background: #2563EB;
        }

        @keyframes blobMove {
            0%, 100% {
                transform: translate(0, 0) scale(1);
            }
            50% {
                transform: translate(10px, 12px) scale(1.12);
            }
        }

        .card-icon {
            position: relative;
            z-index: 2;
            width: 62px;
            height: 62px;
            border-radius: 18px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            margin-bottom: 20px;
            animation: floatIcon 3.5s ease-in-out infinite;
        }

        .card-one .card-icon {
            background: rgba(96, 165, 250, 0.15);
        }

        .card-two .card-icon {
            background: rgba(34, 197, 94, 0.14);
        }

        .card-three .card-icon {
            background: rgba(37, 99, 235, 0.14);
        }

        @keyframes floatIcon {
            0%, 100% {
                transform: translateY(0);
            }
            50% {
                transform: translateY(-6px);
            }
        }

        .card-title {
            position: relative;
            z-index: 2;
            font-size: 23px;
            font-weight: 700;
            color: #1E293B;
            line-height: 1.35;
            margin-bottom: 14px;
        }

        .card-text {
            position: relative;
            z-index: 2;
            font-size: 15px;
            line-height: 1.7;
            color: #64748B;
            margin-bottom: 22px;
        }

        .card-tag {
            position: relative;
            z-index: 2;
            display: inline-block;
            padding: 9px 16px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 600;
            color: #2563EB;
            background: #EFF6FF;
        }

        .card-two .card-tag {
            color: #15803D;
            background: #F0FDF4;
        }

        .card-three .card-tag {
            color: #1D4ED8;
            background: #DBEAFE;
        }

        .footer-note {
            margin-top: 26px;
            color: #64748B;
            font-size: 14px;
        }

        @media (max-width: 980px) {
            .cards-grid {
                grid-template-columns: 1fr;
            }

            .brand-title {
                font-size: 44px;
            }

            .main-box {
                padding: 35px 22px;
            }
        }

        @media (max-width: 520px) {
            .brand-title {
                font-size: 36px;
            }

            .subtitle {
                font-size: 15px;
            }

            .user-card {
                min-height: 220px;
            }
        }
    </style>
</head>
<body>
    <div class="page-wrapper">
        <div class="main-box">
            <div class="logo-area">
                <div class="logo-badge">
                    <span>U</span>
                </div>
                <h1 class="brand-title">UniBook</h1>
                <p class="subtitle">Choose how you want to enter the platform</p>
            </div>

            <form action="entry" method="post">
                <div class="cards-grid">

                    <button class="user-card card-one" type="submit" name="userType" value="new_without_campus">
                        <div class="card-icon">🎓</div>
                        <div class="card-title">New User <br>(Without Campus Registered)</div>
                        <div class="card-text">
                            Best for new users who have not registered their campus yet and need to start from the beginning.
                        </div>
                        <span class="card-tag">Start Here</span>
                    </button>

                    <button class="user-card card-two" type="submit" name="userType" value="new_with_campus">
                        <div class="card-icon">🏫</div>
                        <div class="card-title">New User <br>(With Campus Registered)</div>
                        <div class="card-text">
                            Perfect for users who already have their campus registered and are ready to continue quickly.
                        </div>
                        <span class="card-tag">Continue Setup</span>
                    </button>

                    <button class="user-card card-three" type="submit" name="userType" value="already_registered">
                        <div class="card-icon">👤</div>
                        <div class="card-title">User <br>(Already Registered)</div>
                        <div class="card-text">
                            For existing users who already have an account and want to access the system directly.
                        </div>
                        <span class="card-tag">Sign In</span>
                    </button>

                </div>
            </form>

            <p class="footer-note">UniBook Student Platform • Clean • Smart • Modern</p>
        </div>
    </div>
</body>
</html>