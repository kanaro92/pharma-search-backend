package com.pharmasearch.repository;

import com.pharmasearch.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrderByTimestampDesc(Long senderId, Long receiverId);
    List<Message> findByMedicationRequestIdOrderByTimestampDesc(Long requestId);
    List<Message> findByReceiverIdAndReadFalseOrderByTimestampDesc(Long receiverId);
}
