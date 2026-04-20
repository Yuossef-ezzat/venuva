package com.example.venuva.Core.Domain.Models.EventModule;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.example.venuva.Core.Domain.Models.BaseEntity;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Core.Domain.Models.RegistrationModules.Registration;
import com.example.venuva.Core.Domain.Models.PaymentModule.Payment;
import com.example.venuva.Core.Domain.Models.NotificationModule.Notification;
import com.example.venuva.Shared.Enums.EventStatus;

@Entity
@Table(name = "events")
public class Event extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime date;

    private String location;

    private int maxAttendance;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    private boolean paymentRequired;

    // ===== Relations =====

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Registration> registrations;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    // ===== Getters & Setters =====

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxAttendance() {
        return maxAttendance;
    }

    public void setMaxAttendance(int maxAttendance) {
        this.maxAttendance = maxAttendance;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public boolean isPaymentRequired() {
        return paymentRequired;
    }

    public void setPaymentRequired(boolean paymentRequired) {
        this.paymentRequired = paymentRequired;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}