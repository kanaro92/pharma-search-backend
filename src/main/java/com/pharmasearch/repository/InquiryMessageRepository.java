package com.pharmasearch.repository;

import com.pharmasearch.model.InquiryMessage;
import com.pharmasearch.model.MedicationInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InquiryMessageRepository extends JpaRepository<InquiryMessage, Long> {
    List<InquiryMessage> findByInquiryOrderByCreatedAtAsc(MedicationInquiry inquiry);
}
