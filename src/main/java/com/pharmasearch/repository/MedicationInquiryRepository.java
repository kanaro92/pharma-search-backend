package com.pharmasearch.repository;

import com.pharmasearch.constants.InquiryStatus;
import com.pharmasearch.model.MedicationInquiry;
import com.pharmasearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationInquiryRepository extends JpaRepository<MedicationInquiry, Long> {
    @Query(value = """
            SELECT mi.* FROM medication_inquiries mi 
            WHERE mi.user_id = :userId
            """, nativeQuery = true)
    List<MedicationInquiry> findByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT mi.* FROM medication_inquiries mi 
            JOIN users u ON mi.user_id = u.id 
            WHERE u.id = :#{#user.id}
            """, nativeQuery = true)
    List<MedicationInquiry> findByUser(@Param("user") User user);

    @Query(value = """
            SELECT mi.* FROM medication_inquiries mi 
            JOIN users u ON mi.user_id = u.id 
            WHERE u.id = :#{#user.id} AND mi.status = :status
            """, nativeQuery = true)
    List<MedicationInquiry> findByUserAndStatus(@Param("user") User user, @Param("status") String status);

    @Query(value = """
            SELECT mi.* FROM medication_inquiries mi 
            WHERE mi.status = :status
            """, nativeQuery = true)
    List<MedicationInquiry> findByStatus(@Param("status") String status);
}
