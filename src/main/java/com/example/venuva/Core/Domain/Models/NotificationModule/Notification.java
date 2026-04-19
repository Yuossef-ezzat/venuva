package com.example.venuva.Core.Domain.Models.NotificationModule;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.example.venuva.Core.Domain.Models.BaseEntity;

@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    private String message;

    private LocalDateTime date;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL)
    private List<UserNotification> userNotifications;

    // ===== Getters & Setters =====

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<UserNotification> getUserNotifications() {
        return userNotifications;
    }

    public void setUserNotifications(List<UserNotification> userNotifications) {
        this.userNotifications = userNotifications;
    }
}