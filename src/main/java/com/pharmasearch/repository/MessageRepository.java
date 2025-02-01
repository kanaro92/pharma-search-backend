package com.pharmasearch.repository;

import com.pharmasearch.model.Message;
import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRequestOrderByCreatedAtAsc(MedicationRequest request);

    @Query("SELECT m FROM Message m WHERE m.request = :request " +
           "AND ((m.sender = :user1 AND m.receiver = :user2) " +
           "OR (m.sender = :user2 AND m.receiver = :user1)) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findMessagesBetweenUsers(MedicationRequest request, User user1, User user2);
}
