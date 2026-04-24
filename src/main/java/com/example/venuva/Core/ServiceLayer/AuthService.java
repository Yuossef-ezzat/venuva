package com.example.venuva.Core.ServiceLayer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.venuva.Core.Domain.Models.UserDetails.Roles;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.UserRepository;
import com.example.venuva.Shared.Dtos.AuthDtos.AuthResponse;
import com.example.venuva.Shared.Dtos.AuthDtos.LoginRequest;
import com.example.venuva.Shared.Dtos.AuthDtos.RegisterRequest;

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
                log.info("AuthService.getCurrentUser() called with email={}", email);

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Email not found"));

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                log.info("AuthService.getCurrentUser() success: Retrieved user {}", email);
                return new AuthResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }

        public AuthResponse login(LoginRequest loginDto) {
                log.info("AuthService.login() called with email={}", loginDto.getEmail());

                User user = userRepository.findByEmail(loginDto.getEmail())
                        .orElseThrow(() -> {
                                log.warn("AuthService.login() failed: Email not found for {}", loginDto.getEmail());
                                return new RuntimeException("Email not found");
                        });

                if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                        log.warn("AuthService.login() failed: Wrong password for email={}", loginDto.getEmail());
                        throw new RuntimeException("Unauthorized");
                }

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                log.info("AuthService.login() success: User {} logged in successfully", loginDto.getEmail());
                return new AuthResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }

        public AuthResponse registerOrganizer(RegisterRequest dto) {
                log.info("AuthService.registerOrganizer() called with username='{}', email={}, role=ORGANIZER", 
                        dto.getUsername(), dto.getEmail());

                User user = User.builder()
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .username(dto.getUsername())
                        .role(Roles.ORGANIZER)
                        .enabled(true)
                        .build();

                userRepository.save(user);

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                log.info("AuthService.registerOrganizer() success: Organizer {} registered", dto.getEmail());
                return new AuthResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }

        public AuthResponse register(RegisterRequest dto) {
                log.info("AuthService.register() called with username='{}', email={}, role=ATTENDEE", 
                        dto.getUsername(), dto.getEmail());

                User user = User.builder()
                        .username(dto.getUsername())
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .role(Roles.ATTENDEE)
                        .enabled(true)
                        .build();
                
                userRepository.save(user);

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                log.info("AuthService.register() success: User {} registered", dto.getEmail());
                return new AuthResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }
}