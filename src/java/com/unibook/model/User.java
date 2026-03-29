package com.unibook.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Application user (maps to {@code users} table).
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private long userId;
    private String firstName;
    private String lastName;
    private String universityCampus;
    private String degreeProgram;
    private String email;
    /** PBKDF2 hash; only populated when needed for authentication — never expose in JSP. */
    private String passwordHash;
    private String profilePicturePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUniversityCampus() {
        return universityCampus;
    }

    public void setUniversityCampus(String universityCampus) {
        this.universityCampus = universityCampus;
    }

    public String getDegreeProgram() {
        return degreeProgram;
    }

    public void setDegreeProgram(String degreeProgram) {
        this.degreeProgram = degreeProgram;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    /**
     * Copy safe for storing in HTTP session (no password hash).
     */
    public User safeCopyForSession() {
        User u = new User();
        u.setUserId(this.userId);
        u.setFirstName(this.firstName);
        u.setLastName(this.lastName);
        u.setUniversityCampus(this.universityCampus);
        u.setDegreeProgram(this.degreeProgram);
        u.setEmail(this.email);
        u.setProfilePicturePath(this.profilePicturePath);
        u.setCreatedAt(this.createdAt);
        u.setUpdatedAt(this.updatedAt);
        return u;
    }
}
