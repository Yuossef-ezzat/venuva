package com.example.venuva.Core.Domain.Models.EventModule;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Core.Domain.Models.RegistrationModules.Registration;
import com.example.venuva.Core.Domain.Models.PaymentModule.Payment;
import com.example.venuva.Shared.Enums.EventStatus;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime date;

    private String location;

    private int maxAttendance;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    private boolean paymentRequired;
    private BigDecimal price;


    // ===== Relations =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Registration> registrations;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Payment> payments;
}