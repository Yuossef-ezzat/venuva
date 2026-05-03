package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.aopmodule.aop.src.main.java.com.example.AOP.Annotation.HandleException;
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
    @PreAuthorize("hasRole('ATTENDEE')")
    @HandleException
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDto requestDto) {
        var result = registrationService.registerUserToEvent(requestDto);
        return ResponseUtility.toResponse(result, HttpStatus.CREATED);
    }

    // =========================
    // Get user registrations
    // =========================
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ATTENDEE') or hasRole('ADMIN')")
    @HandleException
    public ResponseEntity<?> getUserRegistrations(@PathVariable int userId) {
        var result = registrationService.getUserRegistrations(userId);
        return ResponseUtility.toResponse(result);
    }

    // =========================
    // Cancel registration
    // =========================
    @DeleteMapping("/cancel")
    @PreAuthorize("hasRole('ATTENDEE')")
    @HandleException
    public ResponseEntity<?> cancelRegistration(@RequestBody CancleRegisrationDto dto) {
        var result = registrationService.cancelRegistration(dto);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new MessageResponse("You are cancelled successfully"));
        }
        return ResponseUtility.toResponse(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getNumberOfRegesters")
    @HandleException
    public ResponseEntity<?> getNumberOfRegesters() {
        var result = registrationService.getNumberOfRegesters();
        return ResponseUtility.toResponse(result);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    @GetMapping("/getNumberOfRegestersForEvent/{eventId}")
    @HandleException
    public ResponseEntity<?> getNumberOfRegestersForEvent(@PathVariable int eventId) {
        var result = registrationService.getNumberOfRegestersForEvent(eventId);
        return ResponseUtility.toResponse(result);
    }

    @PreAuthorize("hasRole('ATTENDEE')")
    @GetMapping("/getTotalSpents/{userId}")
    @HandleException
    public ResponseEntity<?> getTotalSpents(@PathVariable int userId) {
        var result = registrationService.getTotalSpents(userId);
        return ResponseUtility.toResponse(result);
    }
    
        // simple response class
    public record MessageResponse(String message) {}
}