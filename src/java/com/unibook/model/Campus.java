package com.unibook.model;

public class Campus {
    private String campusName;
    private String campusCode;
    private String location;

    public Campus() {
    }

    public Campus(String campusName, String campusCode, String location) {
        this.campusName = campusName;
        this.campusCode = campusCode;
        this.location = location;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}