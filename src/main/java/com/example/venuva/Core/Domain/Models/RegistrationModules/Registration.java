package com.example.venuva.Core.Domain.Models.RegistrationModules;

import com.example.venuva.Core.Domain.Models.BaseEntity;
import com.example.venuva.Core.Domain.Models.EventModule.*;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Shared.Enums.RegistrationStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "registrations")
public class Registration extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus registrationStatus;

    // ===== Relations =====

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // ===== Getters & Setters =====

    public RegistrationStatus getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}
