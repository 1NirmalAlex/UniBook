package com.unibook.dao;

import com.unibook.model.Event;
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

public class EventDAO {

    private static final int TITLE_MAX = 255;
    private static final int LOCATION_MAX = 255;

    public long createEvent(long creatorId, String title, String description, LocalDateTime eventDateTime, String location)
            throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new SQLException("Title required.");
        }
        String t = title.trim();
        if (t.length() > TITLE_MAX) {
            throw new SQLException("Title too long.");
        }
        if (eventDateTime == null) {
            throw new SQLException("Date and time required.");
        }
        String loc = location == null ? null : location.trim();
        if (loc != null && loc.length() > LOCATION_MAX) {
            throw new SQLException("Location too long.");
        }
        if (loc != null && loc.isEmpty()) {
            loc = null;
        }

        String sqlIns = "INSERT INTO events (creator_id, title, description, event_datetime, location) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long eventId;
                try (PreparedStatement ps = conn.prepareStatement(sqlIns, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, creatorId);
                    ps.setString(2, t);
                    ps.setString(3, description == null || description.trim().isEmpty() ? null : description.trim());
                    ps.setObject(4, eventDateTime);
                    ps.setString(5, loc);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No event id.");
                        }
                        eventId = keys.getLong(1);
                    }
                }
                String sqlPart = "INSERT IGNORE INTO event_participants (event_id, user_id) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlPart)) {
                    ps.setLong(1, eventId);
                    ps.setLong(2, creatorId);
                    ps.executeUpdate();
                }
                conn.commit();
                return eventId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Event> findUpcomingForViewer(long viewerUserId, int limit) throws SQLException {
        String sql = "SELECT e.event_id, e.creator_id, e.title, e.description, e.event_datetime, e.location, e.created_at, "
                + "u.user_id AS c_uid, u.first_name AS c_first, u.last_name AS c_last, u.profile_picture_path AS c_pic, "
                + "(SELECT COUNT(*) FROM event_participants ep WHERE ep.event_id = e.event_id) AS participant_count, "
                + "EXISTS (SELECT 1 FROM event_participants ep2 WHERE ep2.event_id = e.event_id AND ep2.user_id = ?) AS user_joined "
                + "FROM events e INNER JOIN users u ON u.user_id = e.creator_id "
                + "WHERE e.event_datetime >= NOW() ORDER BY e.event_datetime ASC LIMIT ?";
        List<Event> list = new ArrayList<>();
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
     * Deletes the event and related rows (participants) if {@code userId} is the creator.
     */
    public boolean deleteIfCreator(long eventId, long userId) throws SQLException {
        String sql = "DELETE FROM events WHERE event_id = ? AND creator_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    public Optional<Long> findCreatorId(long eventId) throws SQLException {
        String sql = "SELECT creator_id FROM events WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("creator_id"));
                }
            }
        }
        return Optional.empty();
    }

    public boolean joinEvent(long eventId, long userId) throws SQLException {
        String sql = "INSERT INTO event_participants (event_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
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
     * @return 1 if a row was removed, 0 if not participating / unknown event, -1 if user is the host (cannot leave RSVP)
     */
    public int leaveEvent(long eventId, long userId) throws SQLException {
        Optional<Long> creator = findCreatorId(eventId);
        if (creator.isEmpty()) {
            return 0;
        }
        if (creator.get() == userId) {
            return -1;
        }
        String sql = "DELETE FROM event_participants WHERE event_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1 ? 1 : 0;
        }
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setEventId(rs.getLong("event_id"));
        e.setCreatorId(rs.getLong("creator_id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        Timestamp ev = rs.getTimestamp("event_datetime");
        if (ev != null) {
            e.setEventDateTime(ev.toLocalDateTime());
        }
        e.setLocation(rs.getString("location"));
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) {
            e.setCreatedAt(c.toLocalDateTime());
        }
        User creator = new User();
        creator.setUserId(rs.getLong("c_uid"));
        creator.setFirstName(rs.getString("c_first"));
        creator.setLastName(rs.getString("c_last"));
        creator.setProfilePicturePath(rs.getString("c_pic"));
        e.setCreator(creator);
        e.setParticipantCount(rs.getInt("participant_count"));
        e.setJoinedByCurrentUser(rs.getInt("user_joined") == 1);
        return e;
    }
}
