package com.example.venuva.Core.ServiceLayer;



import lombok.RequiredArgsConstructor;

// import org.apache.el.stream.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.venuva.Core.Domain.Abstractions.Error;
import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Core.Domain.Models.NotificationModule.*;
import com.example.venuva.Core.Domain.Models.RegistrationModules.Registration;
import com.example.venuva.Core.Domain.Models.UserDetails.Roles;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Core.ServiceAbstraction.INotifService;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.*;
import com.example.venuva.Shared.Dtos.Notifs.NotifDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotifService implements INotifService {

    private final EmailService emailService;
    private final UserRepository userNotifRepo;
    // private final NotificationRepository notifRepo;
    // private final UserDetailsService userDetailsService;
    private final IGenericRepository<UserNotification, Integer> userNotifRepoGeneric;
    private final IGenericRepository<Notification, Integer> notifRepo;
    private final IGenericRepository<User, Integer> userRepo;
    @Override
    public Result<List<NotifDTO>> getNotifsById(int id) {
        List<UserNotification> notifs = userNotifRepoGeneric.findAll()
                .stream()
                .filter(n -> n.getUserId() == id)
                .collect(Collectors.toList());

        List<NotifDTO> dtoList = notifs.stream()
                .map(n -> {
                    Notification notif = notifRepo.findById(n.getNotifId()).orElse(null);
                    User user = userRepo.findById(n.getUserId()).orElse(null);
                    if (notif == null) return null;

                    return new NotifDTO(
                            n.getNotifId(),
                            notif.getMessage(),
                            notif.getDate(),
                            n.getUserId(),
                            user != null ? user.getUsername() : "Unknown",
                            n.isRead()
                    );
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList()); 


        return Result.success(dtoList);
    }

    @Override
    public Result<NotifDTO> markNotifAsRead(int notifId) {
        Optional<UserNotification> notifOptional = userNotifRepoGeneric.findById(notifId);
        
        if (!notifOptional.isPresent()) {
            return Result.failure(new Error("Notif.NotFound", "Notification not found"));
        }
        
        UserNotification notif = notifOptional.get();
        User user = userRepo.findById(notif.getUserId()).orElse(null);
        Notification n = notifRepo.findById(notif.getNotifId()).orElse(null);

        if (n == null) {
            return Result.failure(new Error("Notif.NotFound", "Notification not found"));
        }

        notif.setRead(true);
        userNotifRepoGeneric.save(notif);

        return Result.success(new NotifDTO(
                                n.getNotifId(),
                                n.getMessage(),
                                n.getDate(),
                                notif.getUserId(),
                                user != null ? user.getUsername() : "Unknown",
                                notif.isRead()
                        ));
    }

    @Override
    public Result<Object> sendNotification(String message) {

        Notification notif = new Notification();
        notif.setMessage(message);
        notif.setDate(LocalDateTime.now());

        notifRepo.save(notif); 

        Optional<List<User>> users = userRepo.findAll(u -> u.getRole() == Roles.ATTENDEE);

        for (User user : users.get()) {

            UserNotification userNotif = new UserNotification();
            userNotif.setUserId(user.getId());
            userNotif.setNotifId(notif.getNotifId());
            userNotif.setRead(false);

            userNotifRepoGeneric.save(userNotif);

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

        notifRepo.save(notif); 

        for (Registration reg : registrations) {

            UserNotification userNotif = new UserNotification();
            userNotif.setUserId(reg.getUserId());
            userNotif.setNotifId(notif.getNotifId());
            userNotif.setRead(false);

            userNotifRepoGeneric.save(userNotif);

            emailService.sendEmail(
                    reg.getUser().getEmail(),
                    "New Notification",
                    message
            );
        }

        return Result.success(null);
    }
}