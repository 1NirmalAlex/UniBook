package com.unibook.dao;

import com.unibook.model.Achievement;
import com.unibook.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AchievementDAO {

    public List<Achievement> findByUserId(long userId) throws SQLException {
        String sql = "SELECT achievement_id, user_id, title, created_at FROM user_achievements "
                + "WHERE user_id = ? ORDER BY created_at DESC";
        List<Achievement> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public long insert(long userId, String title) throws SQLException {
        String sql = "INSERT INTO user_achievements (user_id, title) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, title.trim());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Insert achievement failed.");
    }

    /**
     * Deletes if the row belongs to {@code userId}.
     */
    public boolean delete(long achievementId, long userId) throws SQLException {
        String sql = "DELETE FROM user_achievements WHERE achievement_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, achievementId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    private Achievement mapRow(ResultSet rs) throws SQLException {
        Achievement a = new Achievement();
        a.setAchievementId(rs.getLong("achievement_id"));
        a.setUserId(rs.getLong("user_id"));
        a.setTitle(rs.getString("title"));
        Timestamp t = rs.getTimestamp("created_at");
        if (t != null) {
            a.setCreatedAt(t.toLocalDateTime());
        }
        return a;
    }
}
