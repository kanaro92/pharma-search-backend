package com.pharmasearch.repository;

import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.model.Patient;
import com.pharmasearch.model.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicationRequestRepository extends JpaRepository<MedicationRequest, Long> {
    List<MedicationRequest> findByPatientOrderByRequestTimeDesc(Patient patient);
    List<MedicationRequest> findByPharmacyOrderByRequestTimeDesc(Pharmacy pharmacy);
}
