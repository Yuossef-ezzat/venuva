package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.ServiceLayer.NotificationService;
import com.example.venuva.Shared.Dtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ===== POST /api/notifications =====
    // Send a notification to a specific user about an event
    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> send(
            @Valid @RequestBody CreateNotificationDto dto) {
        NotificationResponse response = notificationService.send(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===== POST /api/notifications/event/{eventId}/broadcast =====
    // Send a notification to ALL registrants of an event
    @PostMapping("/event/{eventId}/broadcast")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Void> broadcast(
            @PathVariable int eventId,
            @RequestParam String message) {
        notificationService.sendToAllEventRegistrants(eventId, message);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ===== GET /api/notifications/{id} =====
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> getById(@PathVariable int id) {
        NotificationResponse response = notificationService.getById(id);
        return ResponseEntity.ok(response);
    }

    // ===== GET /api/notifications/user/{userId} =====
    // Get all notifications for a specific user (newest first)
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponse>> getByUser(@PathVariable int userId) {
        List<NotificationResponse> notifications = notificationService.getByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // ===== GET /api/notifications/event/{eventId} =====
    // Get all notifications related to an event
    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponse>> getByEvent(@PathVariable int eventId) {
        List<NotificationResponse> notifications = notificationService.getByEvent(eventId);
        return ResponseEntity.ok(notifications);
    }

    // ===== DELETE /api/notifications/{id} =====
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
