package com.pharmasearch.repository;

import com.pharmasearch.model.FcmToken;
import com.pharmasearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByTokenAndIsActiveTrue(String token);

    List<FcmToken> findByUserAndIsActiveTrue(User user);

    Optional<FcmToken> findTopByUserAndIsActiveTrueOrderByLastUsedAtDesc(User user);

    @Modifying
    @Query("UPDATE FcmToken f SET f.isActive = false WHERE f.user = :user AND f.token = :token")
    void deactivateToken(User user, String token);

    @Modifying
    @Query("UPDATE FcmToken f SET f.isActive = false WHERE f.user = :user")
    void deactivateAllTokens(User user);

    @Modifying
    @Query("UPDATE FcmToken f SET f.isActive = false WHERE f.user.id = :userId AND f.token != :token")
    void deactivateAllTokensExceptThis(@Param("userId") Long userId, @Param("token") String token);
}
