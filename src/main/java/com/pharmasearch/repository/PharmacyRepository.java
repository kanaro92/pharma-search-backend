package com.pharmasearch.repository;

import com.pharmasearch.model.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    @Query(value = "SELECT p.*, " +
           "( 6371 * acos( cos( radians(:latitude) ) * cos( radians( p.latitude ) ) " +
           "* cos( radians( p.longitude ) - radians(:longitude) ) + sin( radians(:latitude) ) " +
           "* sin( radians( p.latitude ) ) ) ) AS distance " +
           "FROM pharmacies p " +
           "HAVING distance < :radius " +
           "ORDER BY distance", 
           nativeQuery = true)
    List<Pharmacy> findNearbyPharmacies(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radius") double radiusInKm
    );
}
