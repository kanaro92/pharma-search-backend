package com.pharmasearch.service;

import com.pharmasearch.dto.PharmacyDTO;
import com.pharmasearch.dto.PharmacyStatisticsDTO;
import com.pharmasearch.model.MedicationInquiry;
import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.PharmacyWithDistance;
import com.pharmasearch.repository.MedicationInquiryRepository;
import com.pharmasearch.repository.PharmacyRepository;
import com.pharmasearch.constants.InquiryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final MedicationInquiryRepository medicationInquiryRepository;
    private final PasswordEncoder passwordEncoder;
    private static final double MAX_DISTANCE = 50.0; // 50 km radius
    private static final int MAX_RESULTS = 20;

    @Transactional
    public Pharmacy registerPharmacy(Pharmacy pharmacy) {
        // Encode password before saving
        pharmacy.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
        return pharmacyRepository.save(pharmacy);
    }

    @Transactional(readOnly = true)
    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pharmacy getPharmacy(Long id) {
        return pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<PharmacyWithDistance> findNearbyPharmacies(double latitude, double longitude) {
        return pharmacyRepository.findNearbyPharmacies(latitude, longitude, MAX_DISTANCE, MAX_RESULTS);
    }

    public Pharmacy savePharmacy(Pharmacy pharmacy) {
        return pharmacyRepository.save(pharmacy);
    }

    @Transactional(readOnly = true)
    public PharmacyDTO getCurrentPharmacyDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pharmacy pharmacy = (Pharmacy) auth.getPrincipal();
        
        return PharmacyDTO.builder()
                .id(pharmacy.getId())
                .name(pharmacy.getName())
                .address(pharmacy.getAddress())
                .phoneNumber(pharmacy.getPhoneNumber())
                .email(pharmacy.getEmail())
                .openingHours(pharmacy.getOpeningHours())
                .latitude(pharmacy.getLatitude())
                .longitude(pharmacy.getLongitude())
                .build();
    }

    @Transactional
    public PharmacyDTO updatePharmacyDetails(PharmacyDTO pharmacyDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pharmacy currentPharmacy = (Pharmacy) auth.getPrincipal();
        
        currentPharmacy.setName(pharmacyDTO.getName());
        currentPharmacy.setAddress(pharmacyDTO.getAddress());
        currentPharmacy.setPhoneNumber(pharmacyDTO.getPhoneNumber());
        currentPharmacy.setEmail(pharmacyDTO.getEmail());
        currentPharmacy.setOpeningHours(pharmacyDTO.getOpeningHours());
        currentPharmacy.setLatitude(pharmacyDTO.getLatitude());
        currentPharmacy.setLongitude(pharmacyDTO.getLongitude());
        
        Pharmacy updatedPharmacy = pharmacyRepository.save(currentPharmacy);
        
        return PharmacyDTO.builder()
                .id(updatedPharmacy.getId())
                .name(updatedPharmacy.getName())
                .address(updatedPharmacy.getAddress())
                .phoneNumber(updatedPharmacy.getPhoneNumber())
                .email(updatedPharmacy.getEmail())
                .openingHours(updatedPharmacy.getOpeningHours())
                .latitude(updatedPharmacy.getLatitude())
                .longitude(updatedPharmacy.getLongitude())
                .build();
    }

    @Transactional(readOnly = true)
    public PharmacyStatisticsDTO getPharmacyStatistics() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pharmacy pharmacy = (Pharmacy) auth.getPrincipal();
        
        Long totalInquiries = medicationInquiryRepository.countByPharmacy(pharmacy);
        Long pendingInquiries = medicationInquiryRepository.countByPharmacyAndStatus(pharmacy, InquiryStatus.PENDING);
        Long resolvedInquiries = medicationInquiryRepository.countByPharmacyAndStatus(pharmacy, InquiryStatus.RESPONDED);
        
        return PharmacyStatisticsDTO.builder()
                .totalInquiries(totalInquiries)
                .pendingInquiries(pendingInquiries)
                .resolvedInquiries(resolvedInquiries)
                .build();
    }
}
