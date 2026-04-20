package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.ServiceAbstraction.IRegistrationService;
import com.example.venuva.Shared.Dtos.RegisterationDto.CancleRegisrationDto;
import com.example.venuva.Shared.Dtos.RegisterationDto.RegistrationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
@Validated
public class RegistrationController {

    private final IRegistrationService registrationService;

    // =========================
    // Register user to event
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDto requestDto) {

        var result = registrationService.registerUserToEvent(requestDto);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getError());
        }

        return ResponseEntity.ok(result.getValue());
    }

    // =========================
    // Get user registrations
    // =========================
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('Attendee') or hasRole('Admin')")
    public ResponseEntity<?> getUserRegistrations(@PathVariable int userId) {

        var result = registrationService.getUserRegistrations(userId);

        if (!result.isSuccess()) {
            return ResponseEntity.status(404).body(result.getError());
        }

        return ResponseEntity.ok(result.getValue());
    }

    // =========================
    // Cancel registration
    // =========================
    @DeleteMapping("/cancel")
    public ResponseEntity<?> cancelRegistration(@RequestBody CancleRegisrationDto dto) {

        var result = registrationService.cancelRegistration(dto);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getError());
        }

        return ResponseEntity.ok(
                new MessageResponse("You are cancelled successfully")
        );
    }

    // simple response class
    public record MessageResponse(String message) {}
}