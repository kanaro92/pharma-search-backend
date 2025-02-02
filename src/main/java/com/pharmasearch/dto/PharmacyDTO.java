package com.pharmasearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("phone")
    private String phoneNumber;
    private String email;
    private String openingHours;
    private Double latitude;
    private Double longitude;
}
