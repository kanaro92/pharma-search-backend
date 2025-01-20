package com.pharmasearch.service;

import com.pharmasearch.model.Medication;
import com.pharmasearch.model.MedicationStock;
import com.pharmasearch.repository.MedicationRepository;
import com.pharmasearch.repository.MedicationStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final MedicationRepository medicationRepository;
    private final MedicationStockRepository stockRepository;

    public List<Medication> searchMedications(String query) {
        return medicationRepository.searchMedications(query);
    }

    public List<MedicationStock> findAvailableStocksNearby(
            Long medicationId, double latitude, double longitude, double radiusInKm) {
        return stockRepository.findAvailableStocksNearby(medicationId, latitude, longitude, radiusInKm);
    }

    public Medication getMedicationById(Long id) {
        return medicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Medication not found"));
    }

    public List<MedicationStock> getStocksByPharmacyAndMedication(Long pharmacyId, Long medicationId) {
        return stockRepository.findByPharmacyIdAndMedicationId(pharmacyId, medicationId);
    }
}
