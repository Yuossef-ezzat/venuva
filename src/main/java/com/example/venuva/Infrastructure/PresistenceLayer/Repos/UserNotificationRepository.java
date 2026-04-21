package com.example.venuva.Infrastructure.PresistenceLayer.Repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.venuva.Core.Domain.Models.NotificationModule.UserNotification;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    
}
