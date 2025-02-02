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
    List<MedicationInquiry> findByUser(User user);
    List<MedicationInquiry> findByUserId(Long userId);
    List<MedicationInquiry> findByUserAndStatusNot(User user, String status);
    List<MedicationInquiry> findByRespondingPharmacy(User pharmacy);
    
    @Query("SELECT i FROM MedicationInquiry i WHERE i.status = :status AND " +
           "(i.respondingPharmacy IS NULL OR i.respondingPharmacy = :pharmacy)")
    List<MedicationInquiry> findByStatusAndRespondingPharmacyIsNullOrRespondingPharmacy(
        @Param("status") String status, @Param("pharmacy") User pharmacy);

    @Query("SELECT i FROM MedicationInquiry i WHERE i.status <> :status AND " +
           "(i.respondingPharmacy IS NULL OR i.respondingPharmacy = :pharmacy)")
    List<MedicationInquiry> findByStatusNotAndRespondingPharmacyIsNullOrRespondingPharmacy(
        @Param("status") String status, @Param("pharmacy") User pharmacy);

    // New methods for pharmacy statistics
    @Query("SELECT COUNT(i) FROM MedicationInquiry i WHERE i.respondingPharmacy = :pharmacy")
    Long countByPharmacy(@Param("pharmacy") Pharmacy pharmacy);

    @Query("SELECT COUNT(i) FROM MedicationInquiry i WHERE i.respondingPharmacy = :pharmacy AND i.status = :status")
    Long countByPharmacyAndStatus(@Param("pharmacy") Pharmacy pharmacy, @Param("status") String status);
}
