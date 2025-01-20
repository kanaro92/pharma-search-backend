package com.pharmasearch.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "pharmacies")
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private String phoneNumber;

    /*
    @OneToMany(mappedBy = "pharmacy")
    private List<Pharmacist> pharmacists;

     */

    @OneToMany(mappedBy = "pharmacy")
    private List<MedicationStock> medicationStocks;
}
