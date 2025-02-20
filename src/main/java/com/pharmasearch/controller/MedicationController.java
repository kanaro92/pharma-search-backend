package com.pharmasearch.controller;

import com.pharmasearch.model.Medication;
import com.pharmasearch.model.MedicationStock;
import com.pharmasearch.model.User;
import com.pharmasearch.service.MedicationService;
import com.pharmasearch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MedicationController {
    private final MedicationService medicationService;
    private final UserService userService;

    /*
    @GetMapping("/search")
    public ResponseEntity<List<Medication>> searchMedications(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0.0") double latitude,
            @RequestParam(required = false, defaultValue = "0.0") double longitude) {

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        List<Medication> medications = medicationService.searchMedications(query, latitude, longitude, currentUser);
        return ResponseEntity.ok(medications);
    }*/

    @GetMapping("/{id}/available-stocks")
    public ResponseEntity<List<MedicationStock>> findAvailableStocks(
            @PathVariable Long id,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radius) {
        return ResponseEntity.ok(
            medicationService.findAvailableStocksNearby(id, latitude, longitude, radius)
        );
    }

    @GetMapping("/{id}/stocks/{pharmacyId}")
    public ResponseEntity<List<MedicationStock>> getStocksByPharmacy(
            @PathVariable Long id,
            @PathVariable Long pharmacyId) {
        return ResponseEntity.ok(
            medicationService.getStocksByPharmacyAndMedication(pharmacyId, id)
        );
    }
}
