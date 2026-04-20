package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.ServiceLayer.RegistrationService;
import com.example.venuva.Shared.Dtos.*;
import com.example.venuva.Shared.Enums.RegistrationStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    // ===== POST /api/registrations =====
    // Register current user to an event
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody CreateRegistrationDto dto) {
        RegistrationResponse response = registrationService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===== GET /api/registrations/{id} =====
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<RegistrationResponse> getById(@PathVariable int id) {
        RegistrationResponse response = registrationService.getById(id);
        return ResponseEntity.ok(response);
    }

    // ===== GET /api/registrations/user/{userId} =====
    // Get all registrations for a specific user
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<List<RegistrationResponse>> getByUser(@PathVariable int userId) {
        List<RegistrationResponse> registrations = registrationService.getByUser(userId);
        return ResponseEntity.ok(registrations);
    }

    // ===== GET /api/registrations/event/{eventId} =====
    // Get all registrations for a specific event (organizer/admin only)
    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<List<RegistrationResponse>> getByEvent(@PathVariable int eventId) {
        List<RegistrationResponse> registrations = registrationService.getByEvent(eventId);
        return ResponseEntity.ok(registrations);
    }

    // ===== PUT /api/registrations/{id}/status =====
    // Update registration status (e.g. after payment confirmation)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<RegistrationResponse> updateStatus(
            @PathVariable int id,
            @RequestParam RegistrationStatus status) {
        RegistrationResponse response = registrationService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    // ===== DELETE /api/registrations/{id} =====
    // Cancel a registration
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable int id) {
        registrationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
