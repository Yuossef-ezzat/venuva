package com.example.venuva.Shared.Dtos.AuthDtos;

public class AuthResponse {
    public int Id;
    public String email;
    public String username;
    public String token;
    

    public AuthResponse(int Id,String email, String username, String token) {
        this.email = email;
        this.username = username;
        this.token = token;
        this.Id = Id;
    }
}