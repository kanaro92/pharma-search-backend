package com.pharmasearch.dto;

import lombok.Data;

@Data
public class PharmacyRegistrationDTO {
    private String name;
    private String email;
    private String password;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private String openingHours;
}
