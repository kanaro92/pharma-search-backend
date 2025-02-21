package com.pharmasearch.repository;

import com.pharmasearch.model.InquiryMessage;
import com.pharmasearch.model.MedicationInquiry;
import com.pharmasearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InquiryMessageRepository extends JpaRepository<InquiryMessage, Long> {
    List<InquiryMessage> findByInquiryOrderByCreatedAtAsc(MedicationInquiry inquiry);
    long countByInquiry(MedicationInquiry inquiry);

    @Query("SELECT m FROM InquiryMessage m WHERE m.inquiry.id = :inquiryId AND m.sender.id = :pharmacyId " +
           "ORDER BY m.createdAt DESC")
    List<InquiryMessage> findLatestMessages(@Param("inquiryId") Long inquiryId, @Param("pharmacyId") Long pharmacyId);

    default Optional<InquiryMessage> findLatestMessage(Long inquiryId, Long pharmacyId) {
        return findLatestMessages(inquiryId, pharmacyId).stream().findFirst();
    }

    @Query("SELECT m FROM InquiryMessage m " +
           "JOIN m.inquiry i " +
           "WHERE i.id = :inquiryId AND " +
           "(m.sender.id = :pharmacyId OR " +  
           "(m.sender.id = i.user.id AND :pharmacyId IN (SELECT rp.id FROM i.respondingPharmacies rp))) " + 
           "ORDER BY m.createdAt ASC")
    List<InquiryMessage> findByInquiryIdAndPharmacyIdOrderByCreatedAtAsc(@Param("inquiryId") Long inquiryId, 
                                                                         @Param("pharmacyId") Long pharmacyId);
}
