package com.example.venuva.Shared.Dtos.RegisterationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {

    private int userId;
    private int eventId;
}