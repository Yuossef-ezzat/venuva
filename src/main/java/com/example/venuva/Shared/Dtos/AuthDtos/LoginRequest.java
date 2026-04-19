package com.example.venuva.Shared.Dtos.AuthDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
}