package com.pharmasearch.service;

import com.pharmasearch.dto.MedicationRequestDTO;
import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.model.Patient;
import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.RequestStatus;
import com.pharmasearch.repository.MedicationRequestRepository;
import com.pharmasearch.repository.PharmacyRepository;
import com.pharmasearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationRequestService {

    private final MedicationRequestRepository medicationRequestRepository;
    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;

    @Transactional
    public MedicationRequest createRequest(MedicationRequestDTO requestDTO, Authentication authentication) {
        String username = authentication.getName();
        Patient patient = (Patient) userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Pharmacy pharmacy = pharmacyRepository.findById(requestDTO.getPharmacyId())
                .orElseThrow(() -> new RuntimeException("Pharmacy not found"));

        MedicationRequest request = new MedicationRequest();
        request.setPatient(patient);
        request.setPharmacy(pharmacy);
        request.setMedicationName(requestDTO.getMedicationName());
        request.setNote(requestDTO.getNote());
        request.setQuantity(requestDTO.getQuantity());
        request.setRequestTime(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);

        return medicationRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<MedicationRequest> getPatientRequests(Authentication authentication) {
        String username = authentication.getName();
        Patient patient = (Patient) userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return medicationRequestRepository.findByPatientOrderByRequestTimeDesc(patient);
    }

    @Transactional(readOnly = true)
    public List<MedicationRequest> getPharmacyRequests(Authentication authentication) {
        // TODO: Implement pharmacy-specific request retrieval
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public MedicationRequest getRequest(Long requestId) {
        return medicationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Medication request not found with id: " + requestId));
    }
}
