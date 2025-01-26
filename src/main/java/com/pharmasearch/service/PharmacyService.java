package com.pharmasearch.service;

import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.PharmacyWithDistance;
import com.pharmasearch.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
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
}
