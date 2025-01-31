package com.pharmasearch.repository;

import com.pharmasearch.model.RequestMessage;
import com.pharmasearch.model.MedicationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestMessageRepository extends JpaRepository<RequestMessage, Long> {
    List<RequestMessage> findByRequestOrderByCreatedAtAsc(MedicationRequest request);
}
