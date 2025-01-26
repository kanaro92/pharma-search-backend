package com.pharmasearch.repository;

import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.model.PharmacyWithDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    @Query("SELECT p FROM Pharmacy p WHERE p.id = :id")
    Optional<Pharmacy> findById(@Param("id") Long id);

    @Query("SELECT p FROM Pharmacy p WHERE p.email = :email")
    Optional<Pharmacy> findByEmail(@Param("email") String email);

    @Query(value = """
            WITH pharmacy_distances AS (
                SELECT 
                    p.id,
                    u.name,
                    u.email,
                    p.address,
                    p.latitude,
                    p.longitude,
                    p.opening_hours,
                    p.phone_number,
                    (6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) *
                    cos(radians(p.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(p.latitude)))) AS distance
                FROM users u
                JOIN pharmacies p ON u.id = p.id
                WHERE u.dtype = 'Pharmacy'
            )
            SELECT 
                id as id,
                name as name,
                email as email,
                address as address,
                latitude as latitude,
                longitude as longitude,
                opening_hours as openingHours,
                phone_number as phoneNumber,
                distance as distance
            FROM pharmacy_distances
            WHERE distance <= :maxDistance
            ORDER BY distance ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<PharmacyWithDistance> findNearbyPharmacies(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("maxDistance") double maxDistance,
            @Param("limit") int limit);

    List<Pharmacy> findByLatitudeBetweenAndLongitudeBetween(
            Double latMin, Double latMax,
            Double lonMin, Double lonMax);
}
