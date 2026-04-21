package com.example.venuva.Core.Domain.Models.RegistrationModules;

import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Shared.Enums.RegistrationStatus;

import io.micrometer.observation.Observation.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    private int id;

    private int userId;
    private int eventId;

    private RegistrationStatus registrationStatus;

    private User user;
    private Event event;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}