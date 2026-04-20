package com.example.venuva.Core.ServiceLayer;

import com.example.venuva.Core.Domain.Models.EventModule.*;
import com.example.venuva.Core.Domain.Models.RegistrationModules.Registration;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Infrastructure.PresistenceLayer.Data.Repos.EventRepository;
import com.example.venuva.Infrastructure.PresistenceLayer.Data.Repos.RegistrationRepository;
import com.example.venuva.Infrastructure.PresistenceLayer.Data.Repos.UserRepository;
import com.example.venuva.Shared.Dtos.*;
import com.example.venuva.Shared.Enums.RegistrationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    // ===== Register to Event =====
    public RegistrationResponse register(CreateRegistrationDto dto) {

        if (registrationRepository.existsByUserIdAndEventId(dto.getUserId(), dto.getEventId())) {
            throw new RuntimeException("User is already registered for this event");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        int currentCount = registrationRepository.countByEventId(dto.getEventId());
        if (currentCount >= event.getMaxAttendance()) {
            throw new RuntimeException("Event has reached maximum attendance");
        }

        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setRegistrationStatus(
                event.isPaymentRequired() ? RegistrationStatus.PENDING : RegistrationStatus.PAID
        );

        Registration saved = registrationRepository.save(registration);
        return mapToResponse(saved);
    }

    // ===== Get All Registrations for an Event =====
    public List<RegistrationResponse> getByEvent(int eventId) {
        return registrationRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===== Get All Registrations for a User =====
    public List<RegistrationResponse> getByUser(int userId) {
        return registrationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===== Get Registration by ID =====
    public RegistrationResponse getById(int id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        return mapToResponse(registration);
    }

    // ===== Cancel Registration =====
    public boolean cancel(int id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registrationRepository.delete(registration);
        return true;
    }

    // ===== Update Status (e.g. after payment) =====
    public RegistrationResponse updateStatus(int id, RegistrationStatus status) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registration.setRegistrationStatus(status);
        Registration updated = registrationRepository.save(registration);
        return mapToResponse(updated);
    }

    // ===== Mapper =====
    private RegistrationResponse mapToResponse(Registration r) {
        return new RegistrationResponse(
                r.getId(),
                r.getRegistrationStatus(),
                r.getUser().getId(),
                r.getUser().getUsername(),
                r.getEvent().getId(),
                r.getEvent().getTitle(),
                r.getEvent().getLocation()
        );
    }
}
