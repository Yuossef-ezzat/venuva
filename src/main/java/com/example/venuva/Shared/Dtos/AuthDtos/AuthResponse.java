package com.example.venuva.Shared.Dtos.AuthDtos;

public class AuthResponse {
    public int Id;
    public String email;
    public String role;
    public String token;
    public String refreshToken; // ← ADD THIS

    public AuthResponse(int id, String email, String role, String token, String refreshToken) {
        this.Id = id;
        this.email = email;
        this.role = role;
        this.token = token;
        this.refreshToken = refreshToken; // ← ADD THIS
    }
}