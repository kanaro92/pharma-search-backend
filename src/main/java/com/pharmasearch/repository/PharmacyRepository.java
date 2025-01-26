package com.pharmasearch.repository;

import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.PharmacyWithDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    @Query(value = """
            SELECT p.id as id, 
                   p.name as name,
                   p.address as address,
                   p.latitude as latitude,
                   p.longitude as longitude,
                   p.phone_number as phoneNumber,
                   (6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * 
                   cos(radians(p.longitude) - radians(:longitude)) + 
                   sin(radians(:latitude)) * sin(radians(p.latitude)))) AS distance 
            FROM pharmacies p 
            ORDER BY distance ASC
            """, nativeQuery = true)
    List<PharmacyWithDistance> findNearbyPharmacies(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude);

    List<Pharmacy> findByLatitudeBetweenAndLongitudeBetween(
            Double latMin, Double latMax,
            Double lonMin, Double lonMax);
}
