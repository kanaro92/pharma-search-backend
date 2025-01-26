package com.pharmasearch.repository;

import com.pharmasearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM users WHERE email = :email
            """, nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);
}
