package com.example.venuva.Core.Domain.Models.NotificationModule;

import com.example.venuva.Core.Domain.Models.UserDetails.User;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "user_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    private int notifId;

    private boolean isRead = false;

    // ===== Relations =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notif_id", insertable = false, updatable = false)
    private Notification notification;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}