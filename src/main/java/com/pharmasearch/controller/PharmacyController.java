package com.pharmasearch.controller;

import com.pharmasearch.dto.PharmacyRegistrationRequest;
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

    @GetMapping("/nearby")
    public ResponseEntity<List<Pharmacy>> findNearbyPharmacies(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude) {
        return ResponseEntity.ok(pharmacyService.findNearbyPharmacies(latitude, longitude));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pharmacy> getPharmacy(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPharmacyById(id));
    }

    @PostMapping
    public ResponseEntity<Pharmacy> registerPharmacy(@RequestBody PharmacyRegistrationRequest request) {
        Pharmacy pharmacy = Pharmacy.builder()
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .phoneNumber(request.getPhoneNumber())
                .build();

        return ResponseEntity.ok(pharmacyService.savePharmacy(pharmacy));
    }
}
