package com.example.venuva.Core.ServiceLayer;


import lombok.RequiredArgsConstructor;

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

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Email not found"));

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                return new AuthResponse(
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }

        public AuthResponse login(LoginRequest loginDto) {

                User user = userRepository.findByEmail(loginDto.getEmail())
                        .orElseThrow(() -> new RuntimeException("Email not found"));

                if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                throw new RuntimeException("Unauthorized");
                }

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                return new AuthResponse(
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }

        public AuthResponse registerOrganizer(RegisterRequest dto) {

                User user = User.builder()
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .role(Roles.ORGANIZER)
                        .enabled(true)
                        .build();

                userRepository.save(user);

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                return new AuthResponse(
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }

        public AuthResponse register(RegisterRequest dto) {

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Roles.ATTENDEE)
                .enabled(true)
                .build();
        
        userRepository.save(user);

                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

                return new AuthResponse(
                        user.getEmail(),
                        user.getUsername(),
                        token
                );
        }
}