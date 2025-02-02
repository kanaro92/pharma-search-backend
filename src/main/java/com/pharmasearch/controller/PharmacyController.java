package com.pharmasearch.controller;

import com.pharmasearch.dto.PharmacyDTO;
import com.pharmasearch.dto.PharmacyRegistrationDTO;
import com.pharmasearch.dto.PharmacyStatisticsDTO;
import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.PharmacyWithDistance;
import com.pharmasearch.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PharmacyController {
    private final PharmacyService pharmacyService;

    @PostMapping("/pharmacies/register")
    public ResponseEntity<Pharmacy> registerPharmacy(@RequestBody PharmacyRegistrationDTO registrationDTO) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setName(registrationDTO.getName());
        pharmacy.setEmail(registrationDTO.getEmail());
        pharmacy.setPassword(registrationDTO.getPassword());
        pharmacy.setAddress(registrationDTO.getAddress());
        pharmacy.setLatitude(registrationDTO.getLatitude());
        pharmacy.setLongitude(registrationDTO.getLongitude());
        pharmacy.setPhoneNumber(registrationDTO.getPhoneNumber());
        pharmacy.setOpeningHours(registrationDTO.getOpeningHours());
        pharmacy.setRole("PHARMACIST");

        Pharmacy savedPharmacy = pharmacyService.registerPharmacy(pharmacy);
        return ResponseEntity.ok(savedPharmacy);
    }

    @GetMapping("/pharmacies")
    public ResponseEntity<List<Pharmacy>> getAllPharmacies() {
        return ResponseEntity.ok(pharmacyService.getAllPharmacies());
    }

    @GetMapping("/pharmacies/{id}")
    public ResponseEntity<Pharmacy> getPharmacy(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPharmacy(id));
    }

    @GetMapping("/pharmacies/nearby")
    public ResponseEntity<List<PharmacyWithDistance>> getNearbyPharmacies(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radius) {
        return ResponseEntity.ok(pharmacyService.findNearbyPharmacies(latitude, longitude));
    }

    // New pharmacy management endpoints
    @GetMapping("/pharmacist/pharmacy")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<PharmacyDTO> getCurrentPharmacyDetails() {
        return ResponseEntity.ok(pharmacyService.getCurrentPharmacyDetails());
    }

    @PutMapping("/pharmacist/pharmacy")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<PharmacyDTO> updatePharmacyDetails(@RequestBody PharmacyDTO pharmacyDTO) {
        return ResponseEntity.ok(pharmacyService.updatePharmacyDetails(pharmacyDTO));
    }

    @GetMapping("/pharmacist/statistics")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<PharmacyStatisticsDTO> getPharmacyStatistics() {
        return ResponseEntity.ok(pharmacyService.getPharmacyStatistics());
    }
}
