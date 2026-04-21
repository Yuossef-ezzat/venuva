package com.example.venuva.Core.Domain.Models.RegistrationModules;

import com.example.venuva.Core.Domain.Models.EventModule.Event;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Shared.Enums.RegistrationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity                              // ← أضف
@Table(name = "registrations")       // ← أضف
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id                              // ← أضف
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← أضف
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "event_id")
    private int eventId;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;
}