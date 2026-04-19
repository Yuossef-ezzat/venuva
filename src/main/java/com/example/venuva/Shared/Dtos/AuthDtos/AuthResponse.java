package com.example.venuva.Shared.Dtos.AuthDtos;

public class AuthResponse {
    private String email;
    private String username;
    public String token;
    

    public AuthResponse(String email, String username, String token) {
        this.email = email;
        this.username = username;
        this.token = token;
    }
}