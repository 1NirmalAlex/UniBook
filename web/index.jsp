<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>UniBook - Entry Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f7fb;
            margin: 0;
            padding: 0;
        }
        .container {
            width: 700px;
            margin: 80px auto;
            background: white;
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            text-align: center;
        }
        h1 {
            color: #1976d2;
        }
        .btn-group {
            display: flex;
            flex-direction: column;
            gap: 15px;
            margin-top: 30px;
        }
        button {
            padding: 15px;
            border: none;
            border-radius: 10px;
            background: #2196f3;
            color: white;
            font-size: 16px;
            cursor: pointer;
        }
        button:hover {
            background: #1565c0;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>UniBook</h1>
        <p>Select your user type</p>

        <form action="entry" method="post">
            <div class="btn-group">
                <button type="submit" name="userType" value="new_without_campus">
                    New User (Without Campus Registered)
                </button>

                <button type="submit" name="userType" value="new_with_campus">
                    New User (With Campus Registered)
                </button>

                <button type="submit" name="userType" value="already_registered">
                    User (Already Registered)
                </button>
            </div>
        </form>
    </div>
</body>
</html>