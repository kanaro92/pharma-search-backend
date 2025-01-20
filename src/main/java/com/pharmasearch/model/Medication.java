package com.pharmasearch.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "medications")
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String dosage;

    @Column
    private String manufacturer;

    @OneToMany(mappedBy = "medication")
    private List<MedicationStock> stocks;

    @Column
    private boolean prescriptionRequired;
}
