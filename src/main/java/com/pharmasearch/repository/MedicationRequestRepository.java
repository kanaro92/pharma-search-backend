package com.pharmasearch.repository;

import com.pharmasearch.model.MedicationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRequestRepository extends JpaRepository<MedicationRequest, Long> {
    // Add custom queries here if needed
}
