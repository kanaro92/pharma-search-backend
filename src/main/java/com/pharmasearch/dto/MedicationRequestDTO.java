package com.pharmasearch.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class MedicationRequestDTO {
    @NotNull(message = "Medication name is required")
    private String medicationName;

    @NotNull(message = "Medication ID is required")
    private Long medicationId;

    @NotNull(message = "Pharmacy ID is required")
    private Long pharmacyId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String note;
}
