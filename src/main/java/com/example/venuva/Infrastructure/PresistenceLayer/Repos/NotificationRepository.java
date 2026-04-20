package com.example.venuva.Infrastructure.PresistenceLayer.Repos;

import com.example.venuva.Core.Domain.Models.NotificationModule.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserIdOrderByDateDesc(int userId);

    List<Notification> findByEventId(int eventId);
}
