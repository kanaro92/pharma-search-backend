package com.pharmasearch.service;

import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.PharmacyWithDistance;
import com.pharmasearch.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private static final Logger log = LoggerFactory.getLogger(PharmacyService.class);

    public List<Pharmacy> findNearbyPharmacies(double latitude, double longitude) {
        try {
            List<PharmacyWithDistance> pharmaciesWithDistance = pharmacyRepository.findNearbyPharmacies(latitude, longitude);
            return pharmaciesWithDistance.stream()
                .map(pwd -> {
                    Pharmacy pharmacy = new Pharmacy();
                    pharmacy.setId(pwd.getId());
                    pharmacy.setName(pwd.getName());
                    pharmacy.setAddress(pwd.getAddress());
                    pharmacy.setLatitude(pwd.getLatitude());
                    pharmacy.setLongitude(pwd.getLongitude());
                    pharmacy.setPhoneNumber(pwd.getPhoneNumber());
                    pharmacy.setDistance(pwd.getDistance());
                    return pharmacy;
                })
                .collect(Collectors.toList());
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
