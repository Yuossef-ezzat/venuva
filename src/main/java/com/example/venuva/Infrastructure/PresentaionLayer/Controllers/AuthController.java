package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.ServiceLayer.AuthService;
import com.example.venuva.Shared.Dtos.AuthDtos.AuthResponse;
import com.example.venuva.Shared.Dtos.AuthDtos.LoginRequest;
import com.example.venuva.Shared.Dtos.AuthDtos.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("[PUBLIC] AuthController.login() — Action: User login attempt");
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("[PUBLIC] AuthController.register() — Action: New user registration");
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register/organizer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AuthResponse> registerOrganizer(
            @Valid @RequestBody RegisterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("[ADMIN] AuthController.registerOrganizer() — User: {}", userDetails.getUsername());
        AuthResponse response = authService.registerOrganizer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponse> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("[USER] AuthController.getCurrentUser() — User: {}", userDetails.getUsername());
        AuthResponse response = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        log.info("[PUBLIC] AuthController.checkEmail() — Checking email existence");
        boolean exists = authService.checkEmail(email);
        return ResponseEntity.ok(exists);
    }
}
