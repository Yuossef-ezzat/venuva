package com.example.venuva.Core.Domain.Models.RegistrationModules;

import org.springframework.boot.ApplicationArguments;


import com.example.venuva.Core.Domain.Models.BaseEntity;
import com.example.venuva.Core.Domain.Models.EventModule.Event;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Shared.Enums.RegistrationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration extends BaseEntity {

    private int userId;
    private int eventId;

    private RegistrationStatus registrationStatus;

    private User user;
    private Event event;
}