package com.unibook.dao;

import com.unibook.model.KuppiyaSession;
import com.unibook.model.User;
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
import java.util.Optional;

public class KuppiyaDAO {

    private static final int SUBJECT_MAX = 255;

    public long createSession(long hostId, String subject, String description, LocalDateTime sessionDateTime)
            throws SQLException {
        if (subject == null || subject.trim().isEmpty()) {
            throw new SQLException("Subject required.");
        }
        String s = subject.trim();
        if (s.length() > SUBJECT_MAX) {
            throw new SQLException("Subject too long.");
        }
        if (sessionDateTime == null) {
            throw new SQLException("Date and time required.");
        }

        String sqlIns = "INSERT INTO kuppiya_sessions (host_id, subject, description, session_datetime) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long sessionId;
                try (PreparedStatement ps = conn.prepareStatement(sqlIns, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, hostId);
                    ps.setString(2, s);
                    ps.setString(3, description == null || description.trim().isEmpty() ? null : description.trim());
                    ps.setObject(4, sessionDateTime);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No session id.");
                        }
                        sessionId = keys.getLong(1);
                    }
                }
                String sqlPart = "INSERT IGNORE INTO kuppiya_participants (session_id, user_id) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlPart)) {
                    ps.setLong(1, sessionId);
                    ps.setLong(2, hostId);
                    ps.executeUpdate();
                }
                conn.commit();
                return sessionId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<KuppiyaSession> findUpcomingForViewer(long viewerUserId, int limit) throws SQLException {
        String sql = "SELECT ks.session_id, ks.host_id, ks.subject, ks.description, ks.session_datetime, ks.created_at, "
                + "u.user_id AS h_uid, u.first_name AS h_first, u.last_name AS h_last, u.profile_picture_path AS h_pic, "
                + "(SELECT COUNT(*) FROM kuppiya_participants kp WHERE kp.session_id = ks.session_id) AS participant_count, "
                + "EXISTS (SELECT 1 FROM kuppiya_participants kp2 WHERE kp2.session_id = ks.session_id AND kp2.user_id = ?) AS user_joined "
                + "FROM kuppiya_sessions ks INNER JOIN users u ON u.user_id = ks.host_id "
                + "WHERE ks.session_datetime >= NOW() ORDER BY ks.session_datetime ASC LIMIT ?";
        List<KuppiyaSession> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, viewerUserId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Deletes the session and participants if {@code userId} is the host.
     */
    public boolean deleteIfHost(long sessionId, long userId) throws SQLException {
        String sql = "DELETE FROM kuppiya_sessions WHERE session_id = ? AND host_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    public Optional<Long> findHostId(long sessionId) throws SQLException {
        String sql = "SELECT host_id FROM kuppiya_sessions WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("host_id"));
                }
            }
        }
        return Optional.empty();
    }

    public boolean joinSession(long sessionId, long userId) throws SQLException {
        String sql = "INSERT INTO kuppiya_participants (session_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062 || "23000".equals(ex.getSQLState())) {
                return false;
            }
            throw ex;
        }
    }

    /**
     * @return 1 if removed, 0 if not in list / unknown session, -1 if user is the host
     */
    public int leaveSession(long sessionId, long userId) throws SQLException {
        Optional<Long> host = findHostId(sessionId);
        if (host.isEmpty()) {
            return 0;
        }
        if (host.get() == userId) {
            return -1;
        }
        String sql = "DELETE FROM kuppiya_participants WHERE session_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1 ? 1 : 0;
        }
    }

    private KuppiyaSession mapRow(ResultSet rs) throws SQLException {
        KuppiyaSession ks = new KuppiyaSession();
        ks.setSessionId(rs.getLong("session_id"));
        ks.setHostId(rs.getLong("host_id"));
        ks.setSubject(rs.getString("subject"));
        ks.setDescription(rs.getString("description"));
        Timestamp t = rs.getTimestamp("session_datetime");
        if (t != null) {
            ks.setSessionDateTime(t.toLocalDateTime());
        }
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) {
            ks.setCreatedAt(c.toLocalDateTime());
        }
        User host = new User();
        host.setUserId(rs.getLong("h_uid"));
        host.setFirstName(rs.getString("h_first"));
        host.setLastName(rs.getString("h_last"));
        host.setProfilePicturePath(rs.getString("h_pic"));
        ks.setHost(host);
        ks.setParticipantCount(rs.getInt("participant_count"));
        ks.setJoinedByCurrentUser(rs.getInt("user_joined") == 1);
        return ks;
    }
}
