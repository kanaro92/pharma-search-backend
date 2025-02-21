package com.pharmasearch.repository;

import com.pharmasearch.constants.InquiryStatus;
import com.pharmasearch.model.MedicationInquiry;
import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationInquiryRepository extends JpaRepository<MedicationInquiry, Long> {
    List<MedicationInquiry> findByUserOrderByCreatedAtDesc(User user);
    List<MedicationInquiry> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<MedicationInquiry> findByUserAndStatusNotOrderByCreatedAtDesc(User user, String status);
    
    @Query("SELECT DISTINCT i FROM MedicationInquiry i JOIN i.respondingPharmacies p WHERE p = :pharmacy ORDER BY i.createdAt DESC")
    List<MedicationInquiry> findByRespondingPharmaciesContainingOrderByCreatedAtDesc(User pharmacy);
    
    @Query("SELECT i FROM MedicationInquiry i WHERE i.status = :status AND " +
           "(:pharmacy MEMBER OF i.respondingPharmacies OR SIZE(i.respondingPharmacies) = 0) " +
           "ORDER BY i.createdAt DESC")
    List<MedicationInquiry> findByStatusAndRespondingPharmaciesEmptyOrContaining(
        @Param("status") String status, @Param("pharmacy") User pharmacy);

    @Query("SELECT i FROM MedicationInquiry i WHERE i.status <> :status AND " +
           "(:pharmacy MEMBER OF i.respondingPharmacies OR SIZE(i.respondingPharmacies) = 0) " +
           "ORDER BY i.createdAt DESC")
    List<MedicationInquiry> findByStatusNotAndRespondingPharmaciesEmptyOrContaining(
        @Param("status") String status, @Param("pharmacy") User pharmacy);

    // Methods for pharmacy statistics
    @Query("SELECT COUNT(DISTINCT i) FROM MedicationInquiry i JOIN i.respondingPharmacies p WHERE p = :pharmacy")
    Long countByPharmacy(@Param("pharmacy") Pharmacy pharmacy);

    @Query("SELECT COUNT(DISTINCT i) FROM MedicationInquiry i JOIN i.respondingPharmacies p WHERE p = :pharmacy AND i.status = :status")
    Long countByPharmacyAndStatus(@Param("pharmacy") Pharmacy pharmacy, @Param("status") String status);
}
