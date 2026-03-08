package com.unibook.service;

import com.unibook.model.Campus;

public class CampusService {

    public boolean registerCampus(Campus campus) {
        return campus != null
                && campus.getCampusName() != null && !campus.getCampusName().trim().isEmpty()
                && campus.getCampusCode() != null && !campus.getCampusCode().trim().isEmpty()
                && campus.getLocation() != null && !campus.getLocation().trim().isEmpty();
    }
}