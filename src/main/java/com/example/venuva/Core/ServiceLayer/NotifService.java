package com.example.venuva.Core.ServiceLayer;



import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Core.Domain.Models.NotificationModule.*;
import com.example.venuva.Core.Domain.Models.RegistrationModules.Registration;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Core.ServiceAbstraction.INotifService;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.*;
import com.example.venuva.Shared.Dtos.NotifDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotifService implements INotifService {

    private final EmailService emailService;
    private final UserRepository userNotifRepo;
    private final NotificationRepository notifRepo;
    private final UserDetailsService userDetailsService;

    @Override
    public Result<List<NotifDTO>> getNotifsById(int id) {

        List<UserNotification> notifs =
                userNotifRepo.findAll(n -> n.getUserId() == id && !n.isRead());

        List<NotifDTO> dtoList = notifs.stream()
                .map(n -> new NotifDTO(
                        n.getNotifId(),
                        n.getUserId(),
                        n.isRead()
                ))
                .collect(Collectors.toList());

        return Result.success(dtoList);
    }

    @Override
    public Result<NotifDTO> markNotifAsRead(int notifId) {

        UserNotification notif = userNotifRepo.find(n -> n.getNotifId() == notifId);

        if (notif == null)
            return Result.failure(new Error("Notif.NotFound", "Notification not found"));

        notif.setRead(true);
        userNotifRepo.update(notif);

        return Result.success(new NotifDTO(
                notif.getNotifId(),
                notif.getUserId(),
                notif.isRead()
        ));
    }

    @Override
    public Result<Object> sendNotification(String message) {

        Notification notif = new Notification();
        notif.setMessage(message);
        notif.setDate(LocalDateTime.now());

        notifRepo.add(notif); // save first to generate ID

        List<User> users = userDetailsService.getUsersInRole("Attendee");

        for (User user : users) {

            UserNotification userNotif = new UserNotification();
            userNotif.setUserId(user.getId());
            userNotif.setNotifId(notif.getId());
            userNotif.setRead(false);

            userNotifRepo.add(userNotif);

            emailService.sendEmail(
                    user.getEmail(),
                    "New Notification",
                    message
            );
        }

        return Result.success(null);
    }

    @Override
    public Result<Object> sendNotificationForRegisterdUserAtEvent(List<Registration> registrations, String message) {

        Notification notif = new Notification();
        notif.setMessage(message);
        notif.setDate(LocalDateTime.now());

        notifRepo.add(notif); // generate ID first

        for (Registration reg : registrations) {

            UserNotification userNotif = new UserNotification();
            userNotif.setUserId(reg.getUserId());
            userNotif.setNotifId(notif.getId());
            userNotif.setRead(false);

            userNotifRepo.add(userNotif);

            emailService.sendEmail(
                    reg.getUser().getEmail(),
                    "New Notification",
                    message
            );
        }

        return Result.success(null);
    }
}