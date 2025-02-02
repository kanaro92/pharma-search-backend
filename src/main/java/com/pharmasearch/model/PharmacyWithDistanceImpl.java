package com.pharmasearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyWithDistanceImpl implements PharmacyWithDistance {
    private Long id;
    private String name;
    private String email;
    private String address;
    private double latitude;
    private double longitude;
    private String openingHours;
    private String phoneNumber;
    private Double distance;

    public PharmacyWithDistanceImpl(Pharmacy pharmacy, Double distance) {
        this.id = pharmacy.getId();
        this.name = pharmacy.getName();
        this.email = pharmacy.getEmail();
        this.address = pharmacy.getAddress();
        this.latitude = pharmacy.getLatitude();
        this.longitude = pharmacy.getLongitude();
        this.openingHours = pharmacy.getOpeningHours();
        this.phoneNumber = pharmacy.getPhoneNumber();
        this.distance = distance;
    }
}
