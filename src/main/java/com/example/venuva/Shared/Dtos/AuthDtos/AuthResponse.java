package com.example.venuva.Shared.Dtos.AuthDtos;

public class AuthResponse {
    public int Id;
    public String email;
    public String role;
    public String token;
    

    public AuthResponse(int Id,String email, String role, String token) {
        this.email = email;
        this.role = role;
        this.token = token;
        this.Id = Id;
    }
}