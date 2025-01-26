package com.pharmasearch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pharmacies")
@DiscriminatorValue("Pharmacy")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Pharmacy extends User {
    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<MedicationRequest> medicationRequests = new HashSet<>();

    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<MedicationStock> medicationStocks = new HashSet<>();

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Transient
    private Double distance;

    public static class PharmacyBuilder extends UserBuilder {
        PharmacyBuilder() {
            super();
            enabled(true);
        }
    }
}
