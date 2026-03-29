package com.unibook.dao;

import com.unibook.model.FriendRelation;
import com.unibook.model.FriendRequest;
import com.unibook.model.User;
import com.unibook.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {

    private static User mapPublicUser(ResultSet rs) throws SQLException {
        User u = mapPublicUserBrief(rs);
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

    /** User row when {@code created_at}/{@code updated_at} are not selected or would collide with join aliases. */
    private static User mapPublicUserBrief(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getLong("user_id"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setUniversityCampus(rs.getString("university_campus"));
        u.setDegreeProgram(rs.getString("degree_program"));
        u.setEmail(rs.getString("email"));
        u.setProfilePicturePath(rs.getString("profile_picture_path"));
        return u;
    }

    private static FriendRequest mapRequestRow(ResultSet rs) throws SQLException {
        FriendRequest fr = new FriendRequest();
        fr.setRequestId(rs.getLong("request_id"));
        fr.setSenderId(rs.getLong("sender_id"));
        fr.setReceiverId(rs.getLong("receiver_id"));
        fr.setStatus(rs.getString("status"));
        Timestamp t = rs.getTimestamp("created_at");
        if (t != null) {
            fr.setCreatedAt(t.toLocalDateTime());
        }
        Timestamp r = rs.getTimestamp("responded_at");
        if (r != null) {
            fr.setRespondedAt(r.toLocalDateTime());
        }
        return fr;
    }

    public FriendRelation getRelation(long currentUserId, long otherUserId) throws SQLException {
        if (currentUserId == otherUserId) {
            return FriendRelation.NONE;
        }
        long lo = Math.min(currentUserId, otherUserId);
        long hi = Math.max(currentUserId, otherUserId);
        String sqlFriend = "SELECT 1 FROM friends WHERE user_id = ? AND friend_user_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlFriend)) {
            ps.setLong(1, lo);
            ps.setLong(2, hi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return FriendRelation.FRIEND;
                }
            }
        }
        String sqlPend = "SELECT sender_id, receiver_id FROM friend_requests WHERE status = 'pending' "
                + "AND ((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)) LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlPend)) {
            ps.setLong(1, currentUserId);
            ps.setLong(2, otherUserId);
            ps.setLong(3, otherUserId);
            ps.setLong(4, currentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long s = rs.getLong("sender_id");
                    if (s == currentUserId) {
                        return FriendRelation.REQUEST_SENT;
                    }
                    return FriendRelation.REQUEST_RECEIVED;
                }
            }
        }
        return FriendRelation.NONE;
    }

    /**
     * Search users by name or email; excludes self. Limited to 40 rows.
     */
    public List<User> searchUsers(long currentUserId, String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String q = "%" + query.trim().replace("%", "\\%").replace("_", "\\_") + "%";
        String sql = "SELECT user_id, first_name, last_name, university_campus, degree_program, email, "
                + "profile_picture_path, created_at, updated_at FROM users WHERE user_id <> ? AND ("
                + "LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) "
                + "OR LOWER(CONCAT(first_name, ' ', last_name)) LIKE LOWER(?)"
                + ") ORDER BY last_name, first_name LIMIT 40";
        List<User> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, currentUserId);
            ps.setString(2, q);
            ps.setString(3, q);
            ps.setString(4, q);
            ps.setString(5, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPublicUser(rs));
                }
            }
        }
        return list;
    }

    /**
     * Suggest users from the same campus (not friends, no pending request).
     */
    public List<User> suggestUsers(long currentUserId, int limit) throws SQLException {
        String sql = "SELECT u.user_id, u.first_name, u.last_name, u.university_campus, u.degree_program, u.email, "
                + "u.profile_picture_path, u.created_at, u.updated_at FROM users u "
                + "INNER JOIN users me ON me.user_id = ? AND u.university_campus = me.university_campus "
                + "WHERE u.user_id <> ? "
                + "AND NOT EXISTS (SELECT 1 FROM friends f WHERE f.user_id = LEAST(?, u.user_id) "
                + "AND f.friend_user_id = GREATEST(?, u.user_id)) "
                + "AND NOT EXISTS (SELECT 1 FROM friend_requests fr WHERE fr.status = 'pending' AND "
                + "((fr.sender_id = ? AND fr.receiver_id = u.user_id) OR (fr.sender_id = u.user_id AND fr.receiver_id = ?))) "
                + "ORDER BY u.last_name, u.first_name LIMIT ?";
        List<User> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, currentUserId);
            ps.setLong(2, currentUserId);
            ps.setLong(3, currentUserId);
            ps.setLong(4, currentUserId);
            ps.setLong(5, currentUserId);
            ps.setLong(6, currentUserId);
            ps.setInt(7, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPublicUser(rs));
                }
            }
        }
        return list;
    }

    public boolean sendRequest(long senderId, long receiverId) throws SQLException {
        if (senderId == receiverId) {
            return false;
        }
        FriendRelation rel = getRelation(senderId, receiverId);
        if (rel != FriendRelation.NONE) {
            return false;
        }
        String sql = "INSERT INTO friend_requests (sender_id, receiver_id, status) VALUES (?, ?, 'pending')";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, senderId);
            ps.setLong(2, receiverId);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                return false;
            }
            throw ex;
        }
    }

    public List<FriendRequest> listIncomingPending(long receiverId) throws SQLException {
        String sql = "SELECT fr.request_id, fr.sender_id, fr.receiver_id, fr.status, fr.created_at, fr.responded_at, "
                + "u.user_id, u.first_name, u.last_name, u.university_campus, u.degree_program, u.email, u.profile_picture_path "
                + "FROM friend_requests fr INNER JOIN users u ON u.user_id = fr.sender_id "
                + "WHERE fr.receiver_id = ? AND fr.status = 'pending' ORDER BY fr.created_at DESC";
        List<FriendRequest> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, receiverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FriendRequest fr = mapRequestRow(rs);
                    User sender = mapPublicUserBrief(rs);
                    fr.setOtherUser(sender);
                    list.add(fr);
                }
            }
        }
        return list;
    }

    public List<FriendRequest> listOutgoingPending(long senderId) throws SQLException {
        String sql = "SELECT fr.request_id, fr.sender_id, fr.receiver_id, fr.status, fr.created_at, fr.responded_at, "
                + "u.user_id, u.first_name, u.last_name, u.university_campus, u.degree_program, u.email, u.profile_picture_path "
                + "FROM friend_requests fr INNER JOIN users u ON u.user_id = fr.receiver_id "
                + "WHERE fr.sender_id = ? AND fr.status = 'pending' ORDER BY fr.created_at DESC";
        List<FriendRequest> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, senderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FriendRequest fr = mapRequestRow(rs);
                    User recv = mapPublicUserBrief(rs);
                    fr.setOtherUser(recv);
                    list.add(fr);
                }
            }
        }
        return list;
    }

    public boolean acceptRequest(long requestId, long receiverId) throws SQLException {
        String sel = "SELECT sender_id, receiver_id, status FROM friend_requests WHERE request_id = ? FOR UPDATE";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long senderId;
                try (PreparedStatement ps = conn.prepareStatement(sel)) {
                    ps.setLong(1, requestId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        if (!"pending".equalsIgnoreCase(rs.getString("status"))) {
                            conn.rollback();
                            return false;
                        }
                        if (rs.getLong("receiver_id") != receiverId) {
                            conn.rollback();
                            return false;
                        }
                        senderId = rs.getLong("sender_id");
                    }
                }
                String upd = "UPDATE friend_requests SET status = 'accepted', responded_at = CURRENT_TIMESTAMP "
                        + "WHERE request_id = ? AND receiver_id = ? AND status = 'pending'";
                try (PreparedStatement ps = conn.prepareStatement(upd)) {
                    ps.setLong(1, requestId);
                    ps.setLong(2, receiverId);
                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }
                long lo = Math.min(senderId, receiverId);
                long hi = Math.max(senderId, receiverId);
                String ins = "INSERT IGNORE INTO friends (user_id, friend_user_id) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(ins)) {
                    ps.setLong(1, lo);
                    ps.setLong(2, hi);
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Removes the pending request so the pair can send a new request later (unique on sender/receiver).
     */
    public boolean rejectRequest(long requestId, long receiverId) throws SQLException {
        String sql = "DELETE FROM friend_requests WHERE request_id = ? AND receiver_id = ? AND status = 'pending'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, requestId);
            ps.setLong(2, receiverId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean cancelOutgoing(long senderId, long receiverId) throws SQLException {
        String sql = "DELETE FROM friend_requests WHERE sender_id = ? AND receiver_id = ? AND status = 'pending'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, senderId);
            ps.setLong(2, receiverId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean cancelOutgoingByRequestId(long requestId, long senderId) throws SQLException {
        String sql = "DELETE FROM friend_requests WHERE request_id = ? AND sender_id = ? AND status = 'pending'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, requestId);
            ps.setLong(2, senderId);
            return ps.executeUpdate() == 1;
        }
    }
}
