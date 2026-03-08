package com.unibook.model;

public class User {
    private String fullName;
    private String email;
    private String password;
    private boolean campusRegistered;
    private boolean alreadyRegistered;

    public User() {
    }

    public User(String fullName, String email, String password, boolean campusRegistered, boolean alreadyRegistered) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.campusRegistered = campusRegistered;
        this.alreadyRegistered = alreadyRegistered;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isCampusRegistered() {
        return campusRegistered;
    }

    public void setCampusRegistered(boolean campusRegistered) {
        this.campusRegistered = campusRegistered;
    }

    public boolean isAlreadyRegistered() {
        return alreadyRegistered;
    }

    public void setAlreadyRegistered(boolean alreadyRegistered) {
        this.alreadyRegistered = alreadyRegistered;
    }
}