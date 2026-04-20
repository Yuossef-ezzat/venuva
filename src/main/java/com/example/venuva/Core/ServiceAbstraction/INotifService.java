package com.example.venuva.Core.ServiceAbstraction;



import java.util.List;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Core.Domain.Models.RegistrationModules.Registration;
import com.example.venuva.Shared.Dtos.NotifDTO;

public interface INotifService {

    Result<Object> sendNotification(String message);

    Result<List<NotifDTO>> getNotifsById(int id);

    Result<NotifDTO> markNotifAsRead(int notifId);

    Result<Object> sendNotificationForRegisterdUserAtEvent(
            List<Registration> registrations,
            String message
    );
}