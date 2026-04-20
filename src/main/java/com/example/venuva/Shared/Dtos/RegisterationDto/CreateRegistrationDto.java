package com.example.venuva.Shared.Dtos.RegisterationDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRegistrationDto {

    @NotNull(message = "User ID is required")
    private int userId;

    @NotNull(message = "Event ID is required")
    private int eventId;
}
