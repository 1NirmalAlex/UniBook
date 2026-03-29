package com.unibook.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Community implements Serializable {

    private static final long serialVersionUID = 1L;

    private long communityId;
    private long creatorId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private User creator;
    private int memberCount;
    private boolean joinedByCurrentUser;

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public boolean isJoinedByCurrentUser() {
        return joinedByCurrentUser;
    }

    public void setJoinedByCurrentUser(boolean joinedByCurrentUser) {
        this.joinedByCurrentUser = joinedByCurrentUser;
    }
}
