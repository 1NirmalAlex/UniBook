package com.unibook.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    private long eventId;
    private long creatorId;
    private String title;
    private String description;
    private LocalDateTime eventDateTime;
    private String location;
    private LocalDateTime createdAt;
    private User creator;
    private boolean joinedByCurrentUser;
    private int participantCount;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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
