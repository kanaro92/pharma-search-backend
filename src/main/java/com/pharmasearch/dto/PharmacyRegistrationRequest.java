package com.pharmasearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyRegistrationRequest {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
}
