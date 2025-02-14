package com.pharmasearch.service;

import com.pharmasearch.model.Medication;
import com.pharmasearch.model.MedicationStock;
import com.pharmasearch.model.User;
import com.pharmasearch.repository.MedicationRepository;
import com.pharmasearch.repository.MedicationStockRepository;
import com.pharmasearch.repository.UserRepository;
import com.pharmasearch.constants.UserRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final MedicationRepository medicationRepository;
    private final MedicationStockRepository stockRepository;
    private final UserRepository userRepository;
    private final FirebaseService firebaseService;

    public List<Medication> searchMedications(String query, double latitude, double longitude) {
        List<Medication> medications = medicationRepository.searchMedications(query);

        if (!medications.isEmpty()) {
            // Récupérer tous les pharmaciens
            List<Long> pharmacistIds = userRepository.findAllByRole(UserRoles.PHARMACIST)
                .stream()
                .map(User::getId)
                .toList();

            // Envoyer une notification à tous les pharmaciens
            firebaseService.sendMedicationSearchNotification(
                pharmacistIds,
                query,
                latitude,
                longitude
            );
        }

        return medications;
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
