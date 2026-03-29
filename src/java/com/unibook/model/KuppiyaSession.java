package com.unibook.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A peer teaching / revision session (kuppiya).
 */
public class KuppiyaSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private long sessionId;
    private long hostId;
    private String subject;
    private String description;
    private LocalDateTime sessionDateTime;
    private LocalDateTime createdAt;
    private User host;
    private boolean joinedByCurrentUser;
    private int participantCount;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getSessionDateTime() {
        return sessionDateTime;
    }

    public void setSessionDateTime(LocalDateTime sessionDateTime) {
        this.sessionDateTime = sessionDateTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public boolean isJoinedByCurrentUser() {
        return joinedByCurrentUser;
    }

    public void setJoinedByCurrentUser(boolean joinedByCurrentUser) {
        this.joinedByCurrentUser = joinedByCurrentUser;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
}
