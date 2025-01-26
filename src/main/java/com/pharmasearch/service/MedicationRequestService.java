package com.pharmasearch.service;

import com.pharmasearch.dto.MedicationRequestDTO;
import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.model.User;
import com.pharmasearch.repository.MedicationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationRequestService {
    private final MedicationRequestRepository requestRepository;

    @Transactional
    public MedicationRequest createRequest(MedicationRequestDTO requestDTO, User user) {
        MedicationRequest request = new MedicationRequest();
        request.setMedicationName(requestDTO.getMedicationName());
        request.setQuantity(requestDTO.getQuantity());
        request.setNote(requestDTO.getNotes()); // Using note instead of notes to match the entity
        request.setUser(user);
        request.setStatus("PENDING");
        return requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<MedicationRequest> getUserRequests(User user) {
        return requestRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<MedicationRequest> getPharmacyRequests(Long pharmacyId, User user) {
        if (!"PHARMACIST".equals(user.getRole())) {
            throw new RuntimeException("Only pharmacists can view pharmacy requests");
        }
        return requestRepository.findByPharmacyId(pharmacyId);
    }

    @Transactional
    public MedicationRequest updateRequestStatus(Long requestId, String status, User user) {
        MedicationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"PHARMACIST".equals(user.getRole())) {
            throw new RuntimeException("Only pharmacists can update request status");
        }

        request.setStatus(status);
        return requestRepository.save(request);
    }

    @Transactional
    public void deleteRequest(Long requestId, User user) {
        MedicationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getUser().getId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("You can only delete your own requests");
        }

        requestRepository.delete(request);
    }

    @Transactional(readOnly = true)
    public MedicationRequest getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }
}
