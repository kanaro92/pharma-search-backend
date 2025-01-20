package com.pharmasearch.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "medication_stocks")
public class MedicationStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @ManyToOne
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @Column
    private String batchNumber;

    @Column
    private java.time.LocalDate expiryDate;
}
