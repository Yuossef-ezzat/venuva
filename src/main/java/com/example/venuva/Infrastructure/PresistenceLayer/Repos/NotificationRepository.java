package com.example.venuva.Infrastructure.PresistenceLayer.Repos;

import com.example.venuva.Core.Domain.Models.NotificationModule.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}
