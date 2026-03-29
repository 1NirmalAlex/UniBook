package com.unibook.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** A user’s membership in a community (for the members list view). */
public class CommunityMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;
    private LocalDateTime joinedAt;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
