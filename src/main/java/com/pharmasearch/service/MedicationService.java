package com.pharmasearch.service;

import com.pharmasearch.model.Medication;
import com.pharmasearch.model.MedicationStock;
import com.pharmasearch.model.User;
import com.pharmasearch.repository.MedicationRepository;
import com.pharmasearch.repository.MedicationStockRepository;
import com.pharmasearch.repository.UserRepository;
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
    private final UserService userService;
/*
    public List<Medication> searchMedications(String query, double latitude, double longitude, User currentUser) {
        List<Medication> medications = medicationRepository.searchMedications(query);

        if (!medications.isEmpty() && currentUser != null) {
            // Send notification to each pharmacist individually
            List<User> pharmacists = userService.getAllPharmacists();
            for (User pharmacist : pharmacists) {
                try {
                    firebaseService.sendMedicationSearchNotification(
                        pharmacist.getId(),
                            savedInquiry.getId(), query,
                        currentUser // Pass the authenticated user who made the search
                    );
                } catch (Exception e) {
                    // Log error but continue with other pharmacists
                    System.err.println("Failed to send notification to pharmacist " + pharmacist.getId() + ": " + e.getMessage());
                }
            }
        }

        return medications;
    }*/

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
