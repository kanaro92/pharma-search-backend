package com.pharmasearch.repository;

import com.pharmasearch.model.Message;
import com.pharmasearch.model.MedicationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRequestOrderByCreatedAtAsc(MedicationRequest request);
}
