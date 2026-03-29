package com.unibook.dao;

import com.unibook.model.Post;
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

public class PostDAO {

    private static final int MAX_CONTENT = 8000;

    public long insert(long authorId, String content) throws SQLException {
        if (content == null || content.trim().isEmpty()) {
            throw new SQLException("Empty post content.");
        }
        String trimmed = content.trim();
        if (trimmed.length() > MAX_CONTENT) {
            throw new SQLException("Post too long.");
        }
        String sql = "INSERT INTO posts (author_id, content) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, authorId);
            ps.setString(2, trimmed);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Insert post failed.");
    }

    /**
     * Posts from the user and their friends (friends table uses min/max user_id pair).
     */
    public List<Post> findTimelineForUser(long userId, int limit) throws SQLException {
        String sql = "SELECT p.post_id, p.author_id, p.content, p.created_at AS post_created, "
                + "u.user_id, u.first_name, u.last_name, u.profile_picture_path "
                + "FROM posts p INNER JOIN users u ON u.user_id = p.author_id "
                + "WHERE p.author_id = ? OR p.author_id IN ("
                + "SELECT CASE WHEN f.user_id = ? THEN f.friend_user_id ELSE f.user_id END "
                + "FROM friends f WHERE f.user_id = ? OR f.friend_user_id = ?"
                + ") ORDER BY p.created_at DESC LIMIT ?";
        List<Post> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, userId);
            ps.setLong(4, userId);
            ps.setInt(5, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Deletes a post only if {@code authorId} is the author.
     */
    public boolean deleteIfAuthor(long postId, long authorId) throws SQLException {
        String sql = "DELETE FROM posts WHERE post_id = ? AND author_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setLong(2, authorId);
            return ps.executeUpdate() == 1;
        }
    }

    private Post mapRow(ResultSet rs) throws SQLException {
        Post p = new Post();
        p.setPostId(rs.getLong("post_id"));
        p.setAuthorId(rs.getLong("author_id"));
        p.setContent(rs.getString("content"));
        Timestamp t = rs.getTimestamp("post_created");
        if (t != null) {
            p.setCreatedAt(t.toLocalDateTime());
        }
        User author = new User();
        author.setUserId(rs.getLong("user_id"));
        author.setFirstName(rs.getString("first_name"));
        author.setLastName(rs.getString("last_name"));
        author.setProfilePicturePath(rs.getString("profile_picture_path"));
        p.setAuthor(author);
        return p;
    }
}
