package com.unibook.dao;

import com.unibook.model.User;
import com.unibook.util.DBConnection;
import com.unibook.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

public class UserDAO {

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public long insertUser(User user, String plainPassword) throws SQLException {
        String sql = "INSERT INTO users (first_name, last_name, university_campus, degree_program, email, password_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String hash = PasswordUtil.hashPassword(plainPassword);
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName().trim());
            ps.setString(2, user.getLastName().trim());
            ps.setString(3, user.getUniversityCampus().trim());
            ps.setString(4, user.getDegreeProgram().trim());
            ps.setString(5, user.getEmail().trim().toLowerCase());
            ps.setString(6, hash);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Insert user failed: no generated key.");
    }

    /**
     * Loads user including password hash (for login verification only).
     */
    public Optional<User> findByEmailWithHash(String email) throws SQLException {
        String sql = "SELECT user_id, first_name, last_name, university_campus, degree_program, email, password_hash, "
                + "profile_picture_path, created_at, updated_at FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFull(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findById(long userId) throws SQLException {
        String sql = "SELECT user_id, first_name, last_name, university_campus, degree_program, email, "
                + "profile_picture_path, created_at, updated_at FROM users WHERE user_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapWithoutPassword(rs));
                }
            }
        }
        return Optional.empty();
    }

    private User mapFull(ResultSet rs) throws SQLException {
        User u = mapWithoutPassword(rs);
        u.setPasswordHash(rs.getString("password_hash"));
        return u;
    }

    private User mapWithoutPassword(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getLong("user_id"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setUniversityCampus(rs.getString("university_campus"));
        u.setDegreeProgram(rs.getString("degree_program"));
        u.setEmail(rs.getString("email"));
        u.setProfilePicturePath(rs.getString("profile_picture_path"));
        Timestamp c = rs.getTimestamp("created_at");
        Timestamp up = rs.getTimestamp("updated_at");
        if (c != null) {
            u.setCreatedAt(c.toLocalDateTime());
        }
        if (up != null) {
            u.setUpdatedAt(up.toLocalDateTime());
        }
        return u;
    }

    public void updateProfile(long userId, String firstName, String lastName, String universityCampus, String degreeProgram)
            throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, university_campus = ?, degree_program = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName.trim());
            ps.setString(2, lastName.trim());
            ps.setString(3, universityCampus.trim());
            ps.setString(4, degreeProgram.trim());
            ps.setLong(5, userId);
            ps.executeUpdate();
        }
    }

    public void updateProfilePicturePath(long userId, String relativePath) throws SQLException {
        String sql = "UPDATE users SET profile_picture_path = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, relativePath);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public Optional<String> findPasswordHashByUserId(long userId) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE user_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String h = rs.getString("password_hash");
                    return Optional.ofNullable(h);
                }
            }
        }
        return Optional.empty();
    }

    public void updatePassword(long userId, String newPlainPassword) throws SQLException {
        String hash = PasswordUtil.hashPassword(newPlainPassword);
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }
}
