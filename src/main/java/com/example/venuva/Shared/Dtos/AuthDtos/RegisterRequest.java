package com.example.venuva.Shared.Dtos.AuthDtos;

import com.example.venuva.Core.Domain.Models.UserDetails.Roles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    public String username;
    public String email;
    public String password;
    public Roles role;
}