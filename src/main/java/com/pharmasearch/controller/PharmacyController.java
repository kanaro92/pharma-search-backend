package com.pharmasearch.controller;

import com.pharmasearch.dto.PharmacyRegistrationDTO;
import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PharmacyController {
    private final PharmacyService pharmacyService;

    @PostMapping("/register")
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

    @GetMapping
    public ResponseEntity<List<Pharmacy>> getAllPharmacies() {
        return ResponseEntity.ok(pharmacyService.getAllPharmacies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pharmacy> getPharmacy(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPharmacy(id));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Pharmacy>> getNearbyPharmacies(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radius) {
        return ResponseEntity.ok(pharmacyService.findNearbyPharmacies(latitude, longitude, radius));
    }
}
