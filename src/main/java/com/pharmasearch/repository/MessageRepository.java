package com.pharmasearch.repository;

import com.pharmasearch.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrderByTimestampDesc(Long senderId, Long receiverId);
    List<Message> findByMedicationRequestIdOrderByTimestampDesc(Long medicationRequestId);
    List<Message> findByReceiverIdAndReadFalseOrderByTimestampDesc(Long receiverId);
}
