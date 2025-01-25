package com.pharmasearch.model;

public interface PharmacyWithDistance {
    Long getId();
    String getName();
    String getAddress();
    double getLatitude();
    double getLongitude();
    String getPhoneNumber();
    Double getDistance();
}
