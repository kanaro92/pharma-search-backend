package com.pharmasearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDTO {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String openingHours;
    private Double latitude;
    private Double longitude;
}
