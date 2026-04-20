package com.example.venuva.Infrastructure.PresistenceLayer.Data.Repos;

import com.example.venuva.Core.Domain.Models.RegistrationModules.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

    boolean existsByUserIdAndEventId(int userId, int eventId);

    int countByEventId(int eventId);

    List<Registration> findByEventId(int eventId);

    List<Registration> findByUserId(int userId);
}
