package com.pharmasearch.repository;

import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRequestRepository extends JpaRepository<MedicationRequest, Long> {
    List<MedicationRequest> findByUser(User user);
    List<MedicationRequest> findByPharmacyId(Long pharmacyId);
    List<MedicationRequest> findByUserAndStatus(User user, String status);
}
