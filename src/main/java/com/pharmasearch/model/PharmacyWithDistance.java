package com.pharmasearch.model;

public interface PharmacyWithDistance {
    Long getId();
    String getName();
    String getEmail();
    String getAddress();
    double getLatitude();
    double getLongitude();
    String getOpeningHours();
    String getPhoneNumber();
    Double getDistance();
}
