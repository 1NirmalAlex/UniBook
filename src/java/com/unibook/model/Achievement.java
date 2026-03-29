package com.unibook.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Achievement implements Serializable {

    private static final long serialVersionUID = 1L;

    private long achievementId;
    private long userId;
    private String title;
    private LocalDateTime createdAt;

    public long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(long achievementId) {
        this.achievementId = achievementId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
