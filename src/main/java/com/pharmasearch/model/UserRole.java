package com.pharmasearch.model;

public enum UserRole {
    PATIENT,
    PHARMACIST,
    ADMIN;

    @Override
    public String toString() {
        return name();
    }
}
