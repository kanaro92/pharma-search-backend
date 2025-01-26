package com.pharmasearch.service;

import com.pharmasearch.dto.MedicationRequestDTO;
import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.User;
import com.pharmasearch.repository.MedicationRequestRepository;
import com.pharmasearch.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationRequestService {
    private final MedicationRequestRepository requestRepository;
    private final PharmacyRepository pharmacyRepository;

    @Transactional
    public MedicationRequest createRequest(MedicationRequestDTO requestDTO, User user) {
        log.debug("Creating medication request for user: {}", user.getEmail());

        if (requestDTO.getMedicationName() == null || requestDTO.getMedicationName().trim().isEmpty()) {
            log.error("Medication name is required");
            throw new IllegalArgumentException("Medication name is required");
        }

        if (requestDTO.getPharmacyId() == null) {
            log.error("Pharmacy ID is required");
            throw new IllegalArgumentException("Pharmacy ID is required");
        }

        try {
            Pharmacy pharmacy = pharmacyRepository.findById(requestDTO.getPharmacyId())
                .orElseThrow(() -> {
                    log.error("Pharmacy not found with ID: {}", requestDTO.getPharmacyId());
                    return new IllegalArgumentException("Pharmacy not found");
                });

            MedicationRequest request = new MedicationRequest();
            request.setMedicationName(requestDTO.getMedicationName().trim());
            request.setQuantity(requestDTO.getQuantity());
            request.setNote(requestDTO.getNotes());
            request.setUser(user);
            request.setPharmacy(pharmacy);
            request.setStatus("PENDING");
            
            MedicationRequest savedRequest = requestRepository.save(request);
            log.info("Successfully created medication request with ID: {}", savedRequest.getId());
            return savedRequest;
        } catch (Exception e) {
            log.error("Failed to create medication request", e);
            throw new RuntimeException("Failed to create medication request", e);
        }
    }

    @Transactional(readOnly = true)
    public MedicationRequest getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));
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

        if (!request.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Only the request owner can delete it");
        }

        requestRepository.delete(request);
    }
}
