package com.unibook.dao;

import com.unibook.model.Community;
import com.unibook.model.CommunityMember;
import com.unibook.model.User;
import com.unibook.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommunityDAO {

    private static final int NAME_MAX = 200;

    public long createCommunity(long creatorId, String name, String description) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new SQLException("Name required.");
        }
        String n = name.trim();
        if (n.length() > NAME_MAX) {
            throw new SQLException("Name too long.");
        }
        String desc = description == null || description.trim().isEmpty() ? null : description.trim();
        if (desc != null && desc.length() > 4000) {
            throw new SQLException("Description too long.");
        }

        String sqlIns = "INSERT INTO communities (creator_id, name, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long communityId;
                try (PreparedStatement ps = conn.prepareStatement(sqlIns, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, creatorId);
                    ps.setString(2, n);
                    ps.setString(3, desc);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No community id.");
                        }
                        communityId = keys.getLong(1);
                    }
                }
                String sqlMem = "INSERT IGNORE INTO community_members (community_id, user_id) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlMem)) {
                    ps.setLong(1, communityId);
                    ps.setLong(2, creatorId);
                    ps.executeUpdate();
                }
                conn.commit();
                return communityId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Community> findAllForViewer(long viewerUserId, int limit) throws SQLException {
        String sql = "SELECT c.community_id, c.creator_id, c.name, c.description, c.created_at, "
                + "u.user_id AS cr_uid, u.first_name AS cr_first, u.last_name AS cr_last, u.profile_picture_path AS cr_pic, "
                + "(SELECT COUNT(*) FROM community_members cm WHERE cm.community_id = c.community_id) AS member_count, "
                + "EXISTS (SELECT 1 FROM community_members cm2 WHERE cm2.community_id = c.community_id AND cm2.user_id = ?) AS user_joined "
                + "FROM communities c INNER JOIN users u ON u.user_id = c.creator_id "
                + "ORDER BY c.name ASC LIMIT ?";
        List<Community> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, viewerUserId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCommunityRow(rs));
                }
            }
        }
        return list;
    }

    public Optional<Community> findById(long communityId, long viewerUserId) throws SQLException {
        String sql = "SELECT c.community_id, c.creator_id, c.name, c.description, c.created_at, "
                + "u.user_id AS cr_uid, u.first_name AS cr_first, u.last_name AS cr_last, u.profile_picture_path AS cr_pic, "
                + "(SELECT COUNT(*) FROM community_members cm WHERE cm.community_id = c.community_id) AS member_count, "
                + "EXISTS (SELECT 1 FROM community_members cm2 WHERE cm2.community_id = c.community_id AND cm2.user_id = ?) AS user_joined "
                + "FROM communities c INNER JOIN users u ON u.user_id = c.creator_id "
                + "WHERE c.community_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, viewerUserId);
            ps.setLong(2, communityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCommunityRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<CommunityMember> findMembers(long communityId) throws SQLException {
        String sql = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.profile_picture_path, cm.joined_at AS member_joined "
                + "FROM community_members cm INNER JOIN users u ON u.user_id = cm.user_id "
                + "WHERE cm.community_id = ? ORDER BY cm.joined_at ASC";
        List<CommunityMember> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, communityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommunityMember m = new CommunityMember();
                    User u = new User();
                    u.setUserId(rs.getLong("user_id"));
                    u.setFirstName(rs.getString("first_name"));
                    u.setLastName(rs.getString("last_name"));
                    u.setEmail(rs.getString("email"));
                    u.setProfilePicturePath(rs.getString("profile_picture_path"));
                    m.setUser(u);
                    Timestamp t = rs.getTimestamp("member_joined");
                    if (t != null) {
                        m.setJoinedAt(t.toLocalDateTime());
                    }
                    list.add(m);
                }
            }
        }
        return list;
    }

    /**
     * Deletes the community and memberships if {@code userId} is the creator.
     */
    public boolean deleteIfCreator(long communityId, long userId) throws SQLException {
        String sql = "DELETE FROM communities WHERE community_id = ? AND creator_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, communityId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    public Optional<Long> findCreatorId(long communityId) throws SQLException {
        String sql = "SELECT creator_id FROM communities WHERE community_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, communityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("creator_id"));
                }
            }
        }
        return Optional.empty();
    }

    public boolean joinCommunity(long communityId, long userId) throws SQLException {
        String sql = "INSERT INTO community_members (community_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, communityId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062 || "23000".equals(ex.getSQLState())) {
                return false;
            }
            throw ex;
        }
    }

    public int leaveCommunity(long communityId, long userId) throws SQLException {
        Optional<Long> creator = findCreatorId(communityId);
        if (creator.isEmpty()) {
            return 0;
        }
        if (creator.get() == userId) {
            return -1;
        }
        String sql = "DELETE FROM community_members WHERE community_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, communityId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1 ? 1 : 0;
        }
    }

    private Community mapCommunityRow(ResultSet rs) throws SQLException {
        Community c = new Community();
        c.setCommunityId(rs.getLong("community_id"));
        c.setCreatorId(rs.getLong("creator_id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        Timestamp cr = rs.getTimestamp("created_at");
        if (cr != null) {
            c.setCreatedAt(cr.toLocalDateTime());
        }
        User creator = new User();
        creator.setUserId(rs.getLong("cr_uid"));
        creator.setFirstName(rs.getString("cr_first"));
        creator.setLastName(rs.getString("cr_last"));
        creator.setProfilePicturePath(rs.getString("cr_pic"));
        c.setCreator(creator);
        c.setMemberCount(rs.getInt("member_count"));
        c.setJoinedByCurrentUser(rs.getInt("user_joined") == 1);
        return c;
    }
}
