package com.example.venuva.Shared.Dtos;

import com.example.venuva.Shared.Enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

    private int id;
    private RegistrationStatus registrationStatus;

    // User info (flat)
    private int userId;
    private String username;

    // Event info (flat)
    private int eventId;
    private String eventTitle;
    private String eventLocation;
}
