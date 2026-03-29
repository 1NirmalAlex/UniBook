package com.unibook.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC connection helper. Configure database settings in {@code db.properties} on the classpath
 * ({@code com/unibook/config/db.properties}).
 */
public final class DBConnection {

    private static final Properties PROPS = new Properties();
    private static volatile boolean loaded;

    private DBConnection() {
    }

    private static void loadProperties() {
        if (loaded) {
            return;
        }
        synchronized (DBConnection.class) {
            if (loaded) {
                return;
            }
            try (InputStream in = DBConnection.class.getResourceAsStream("/com/unibook/config/db.properties")) {
                if (in != null) {
                    PROPS.load(in);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Could not load db.properties", e);
            }
            loaded = true;
        }
    }

    public static Connection getConnection() throws SQLException {
        loadProperties();
        String url = PROPS.getProperty("db.url", "jdbc:mysql://localhost:3306/unibook?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = PROPS.getProperty("db.user", "root");
        String password = PROPS.getProperty("db.password", "");
        try {
            Class.forName(PROPS.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found. Add mysql-connector-j to WEB-INF/lib.", e);
        }
        return DriverManager.getConnection(url, user, password);
    }
}
