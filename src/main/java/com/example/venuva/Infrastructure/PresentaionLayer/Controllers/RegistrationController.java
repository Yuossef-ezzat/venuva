package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.ServiceAbstraction.IRegistrationService;
import com.example.venuva.Infrastructure.Config.ResponseUtility;
import com.example.venuva.Shared.Dtos.RegisterationDto.CancleRegisrationDto;
import com.example.venuva.Shared.Dtos.RegisterationDto.RegistrationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseUtility.toResponse(result, HttpStatus.CREATED);
    }

    // =========================
    // Get user registrations
    // =========================
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ATTENDEE') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserRegistrations(@PathVariable int userId) {
        var result = registrationService.getUserRegistrations(userId);
        return ResponseUtility.toResponse(result);
    }

    // =========================
    // Cancel registration
    // =========================
    @DeleteMapping("/cancel")
    public ResponseEntity<?> cancelRegistration(@RequestBody CancleRegisrationDto dto) {
        var result = registrationService.cancelRegistration(dto);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new MessageResponse("You are cancelled successfully"));
        }
        return ResponseUtility.toResponse(result);
    }

    // simple response class
    public record MessageResponse(String message) {}
}