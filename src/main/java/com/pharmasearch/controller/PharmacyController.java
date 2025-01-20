package com.pharmasearch.controller;

import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {
    private final PharmacyService pharmacyService;

    @GetMapping("/nearby")
    public ResponseEntity<List<Pharmacy>> findNearbyPharmacies(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radius) {
        return ResponseEntity.ok(
            pharmacyService.findNearbyPharmacies(latitude, longitude, radius)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pharmacy> getPharmacy(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPharmacyById(id));
    }
}
