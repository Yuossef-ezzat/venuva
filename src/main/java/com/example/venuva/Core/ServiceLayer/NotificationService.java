package com.example.venuva.Core.ServiceLayer;

import com.example.venuva.Core.Domain.Models.EventModule.*;
import com.example.venuva.Core.Domain.Models.NotificationModule.Notification;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Infrastructure.PresistenceLayer.Data.Repos.EventRepository;
import com.example.venuva.Infrastructure.PresistenceLayer.Data.Repos.NotificationRepository;
import com.example.venuva.Infrastructure.PresistenceLayer.Data.Repos.UserRepository;
import com.example.venuva.Shared.Dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    // ===== Send Notification =====
    public NotificationResponse send(CreateNotificationDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Notification notification = new Notification();
        notification.setMessage(dto.getMessage());
        notification.setDate(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    // ===== Send Notification to All Event Registrants =====
    public void sendToAllEventRegistrants(int eventId, String message) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<User> registrants = event.getRegistrations()
                .stream()
                .map(r -> r.getUser())
                .collect(Collectors.toList());

        for (User user : registrants) {
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setDate(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    // ===== Get Notifications for a User (newest first) =====
    public List<NotificationResponse> getByUser(int userId) {
        return notificationRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===== Get Notifications for an Event =====
    public List<NotificationResponse> getByEvent(int eventId) {
        return notificationRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===== Get Notification by ID =====
    public NotificationResponse getById(int id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        return mapToResponse(notification);
    }

    // ===== Delete Notification =====
    public boolean delete(int id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationRepository.delete(notification);
        return true;
    }

    // ===== Mapper =====
    private NotificationResponse mapToResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getMessage(),
                n.getDate()
        );
    }
}
