package com.pharmasearch.repository;

import com.pharmasearch.model.MedicationInquiry;
import com.pharmasearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationInquiryRepository extends JpaRepository<MedicationInquiry, Long> {
    List<MedicationInquiry> findByUserId(Long userId);
    List<MedicationInquiry> findByUser(User user);
    List<MedicationInquiry> findByUserAndStatus(User user, String status);
}
