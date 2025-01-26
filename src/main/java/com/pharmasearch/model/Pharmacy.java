package com.pharmasearch.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "pharmacies")
public class Pharmacy extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "pharmacy")
    private List<MedicationRequest> medicationRequests;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private String openingHours;

    @Column
    private String phoneNumber;

    @Transient  // This field won't be persisted in the database
    private Double distance;  // Distance in kilometers

    @OneToMany(mappedBy = "pharmacy")
    private List<MedicationStock> medicationStocks;
}
