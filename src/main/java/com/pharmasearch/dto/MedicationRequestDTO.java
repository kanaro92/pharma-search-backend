package com.pharmasearch.dto;

import lombok.Data;

@Data
public class MedicationRequestDTO {
    private String medicationName;
    private Integer quantity;
    private String notes;
    private Long pharmacyId;
}
