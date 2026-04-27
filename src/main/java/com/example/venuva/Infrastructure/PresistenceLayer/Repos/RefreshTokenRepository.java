// src/main/java/com/example/venuva/Infrastructure/PresistenceLayer/Repos/RefreshTokenRepository.java
package com.example.venuva.Infrastructure.PresistenceLayer.Repos;

import com.example.venuva.Core.Domain.Models.UserDetails.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :userId")
    void deleteByUserId(int userId);
}