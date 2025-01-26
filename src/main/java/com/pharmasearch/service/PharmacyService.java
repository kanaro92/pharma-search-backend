package com.pharmasearch.service;

import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.PharmacyWithDistance;
import com.pharmasearch.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(PharmacyService.class);

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
    public List<Pharmacy> findNearbyPharmacies(Double latitude, Double longitude, Double radius) {
        // Calculate the latitude and longitude ranges based on the radius (in kilometers)
        double latRange = radius / 111.0; // 1 degree of latitude is approximately 111 kilometers
        double lonRange = radius / (111.0 * Math.cos(Math.toRadians(latitude)));

        return pharmacyRepository.findByLatitudeBetweenAndLongitudeBetween(
                latitude - latRange, latitude + latRange,
                longitude - lonRange, longitude + lonRange
        );
    }

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

    public Pharmacy savePharmacy(Pharmacy pharmacy) {
        return pharmacyRepository.save(pharmacy);
    }
}
