package com.pharmasearch.service;

import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;

    public List<Pharmacy> findNearbyPharmacies(double latitude, double longitude, double radiusInKm) {
        return pharmacyRepository.findNearbyPharmacies(latitude, longitude, radiusInKm);
    }

    public Pharmacy getPharmacyById(Long id) {
        return pharmacyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pharmacy not found"));
    }
}
