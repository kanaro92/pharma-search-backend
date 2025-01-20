package com.pharmasearch.repository;

import com.pharmasearch.model.MedicationStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MedicationStockRepository extends JpaRepository<MedicationStock, Long> {
    List<MedicationStock> findByPharmacyIdAndMedicationId(Long pharmacyId, Long medicationId);
    
    @Query("SELECT ms FROM MedicationStock ms " +
           "WHERE ms.medication.id = :medicationId " +
           "AND ms.pharmacy.id IN " +
           "(SELECT p.id FROM Pharmacy p WHERE " +
           "( 6371 * acos( cos( radians(:latitude) ) * cos( radians( p.latitude ) ) " +
           "* cos( radians( p.longitude ) - radians(:longitude) ) + sin( radians(:latitude) ) " +
           "* sin( radians( p.latitude ) ) ) ) < :radius)")
    List<MedicationStock> findAvailableStocksNearby(
        @Param("medicationId") Long medicationId,
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radius") double radiusInKm
    );
}
