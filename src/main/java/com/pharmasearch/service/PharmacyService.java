package com.pharmasearch.service;

import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private static final Logger log = LoggerFactory.getLogger(PharmacyService.class);

    public List<Pharmacy> findNearbyPharmacies(double latitude, double longitude) {
        try {
            return pharmacyRepository.findNearbyPharmacies(latitude, longitude);
        } catch (Exception e) {
            log.error("Error finding nearby pharmacies: {}", e.getMessage());
            throw new RuntimeException("Failed to find nearby pharmacies", e);
        }
    }

    public Pharmacy getPharmacyById(Long id) {
        return pharmacyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pharmacy not found"));
    }

    public Pharmacy savePharmacy(Pharmacy pharmacy) {
        return pharmacyRepository.save(pharmacy);
    }
}
