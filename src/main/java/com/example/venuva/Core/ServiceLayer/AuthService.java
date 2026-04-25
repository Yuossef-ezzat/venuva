package com.example.venuva.Core.ServiceLayer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.venuva.Core.Domain.Exceptions.DataConflictException;
import com.example.venuva.Core.Domain.Models.UserDetails.Roles;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.UserRepository;
import com.example.venuva.Shared.Dtos.AuthDtos.AuthResponse;
import com.example.venuva.Shared.Dtos.AuthDtos.LoginRequest;
import com.example.venuva.Shared.Dtos.AuthDtos.RegisterRequest;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

    // ===== Check Email =====
        public boolean checkEmail(String email) {
                return userRepository.findByEmail(email).isPresent();
                }

    // ===== Get Current User =====
        public AuthResponse getCurrentUser(String email) {
                log.info("[START] AuthService.getCurrentUser() — email={}", email);

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Email not found"));

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                log.info("[OK] AuthService.getCurrentUser() — Retrieved user {}", email);
                return new AuthResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }

        public AuthResponse login(LoginRequest loginDto) {
                log.info("[START] AuthService.login() — email={}", loginDto.getEmail());

                User user = userRepository.findByEmail(loginDto.getEmail())
                        .orElseThrow(() -> {
                                log.warn("[WARN] AuthService.login() — Email not found: {}", loginDto.getEmail());
                                return new RuntimeException("Email not found");
                        });

                if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                        log.warn("[WARN] AuthService.login() — Wrong password for email={}", loginDto.getEmail());
                        throw new RuntimeException("Unauthorized");
                }

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                log.info("[OK] AuthService.login() — User {} logged in successfully", loginDto.getEmail());
                return new AuthResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }

        public AuthResponse registerOrganizer(RegisterRequest dto) {
                log.info("[START] AuthService.registerOrganizer() — username='{}', email={}, role=ORGANIZER", 
                        dto.getUsername(), dto.getEmail());

                // Check for duplicate email BEFORE saving
                Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
                if (existingUser.isPresent()) {
                        log.warn("[WARN] AuthService.registerOrganizer() — Email already in use: {}", dto.getEmail());
                        throw new DataConflictException("This email address is already registered. Please use a different email.");
                }

                User user = User.builder()
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .username(dto.getUsername())
                        .role(Roles.ORGANIZER)
                        .enabled(true)
                        .build();

                userRepository.save(user);

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                log.info("[OK] AuthService.registerOrganizer() — Organizer {} registered with role=ORGANIZER", dto.getEmail());
                return new AuthResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }

        public AuthResponse register(RegisterRequest dto) {
                log.info("[START] AuthService.register() — username='{}', email={}, role=ATTENDEE", 
                        dto.getUsername(), dto.getEmail());

                // Check for duplicate email BEFORE saving
                Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
                if (existingUser.isPresent()) {
                        log.warn("[WARN] AuthService.register() — Email already in use: {}", dto.getEmail());
                        throw new DataConflictException("This email address is already registered. Please use a different email.");
                }

                User user = User.builder()
                        .username(dto.getUsername())
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .role(Roles.ATTENDEE)
                        .enabled(true)
                        .build();
                
                userRepository.save(user);

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                log.info("[OK] AuthService.register() — User {} registered with role=ATTENDEE", dto.getEmail());
                return new AuthResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }
}