package com.pharmasearch.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "patients")
public class Patient extends User {
    
    @OneToMany(mappedBy = "user")
    private List<MedicationRequest> medicationRequests;

    @Column
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;
}
