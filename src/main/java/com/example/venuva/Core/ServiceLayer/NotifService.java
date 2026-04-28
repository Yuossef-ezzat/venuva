package com.example.venuva.Core.ServiceLayer;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class NotifService implements INotifService {

    private final EmailService emailService;
    private final UserNotificationRepository userNotifRepoGeneric;
    private final NotificationRepository notifRepo;
    private final UserRepository userRepo;
    @Override
    public Result<List<NotifDTO>> getNotifsById(int id) {
        log.info("NotifService.getNotifsById() called with userId={}", id);
        
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
                            n.getId(),
                            notif.getMessage(),
                            notif.getDate(),
                            n.getUserId(),
                            user != null ? user.getUsername() : "Unknown",
                            n.isRead()
                    );
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList()); 

        log.info("NotifService.getNotifsById() success: {} notifications retrieved for userId={}", dtoList.size(), id);
        return Result.success(dtoList);
    }

    @Override
    public Result<NotifDTO> markNotifAsRead(int notifId) {
        log.info("[START] NotifService.markNotifAsRead() — notifId={}", notifId);
        
        Optional<UserNotification> notifOptional = userNotifRepoGeneric.findById(notifId);
        
        if (!notifOptional.isPresent()) {
            log.warn("[WARN] NotifService.markNotifAsRead() — Notification not found: {}", notifId);
            return Result.failure(new Error("Notif.NotFound", "Notification not found"));
        }
        
        UserNotification notif = notifOptional.get();
        User user = userRepo.findById(notif.getUserId()).orElse(null);
        Notification n = notifRepo.findById(notif.getNotifId()).orElse(null);

        if (n == null) {
            log.warn("[WARN] NotifService.markNotifAsRead() — Notification not found: {}", notifId);
            return Result.failure(new Error("Notif.NotFound", "Notification not found"));
        }

        notif.setRead(true);
        userNotifRepoGeneric.save(notif);

        log.info("[OK] NotifService.markNotifAsRead() — Notification {} marked as read", notifId);
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
        log.info("[START] NotifService.sendNotification() — Broadcasting message");

        Notification notif = new Notification();
        notif.setMessage(message);
        notif.setDate(LocalDateTime.now());

        notifRepo.save(notif);

        Optional<List<User>> users = userRepo.findAll(u -> u.getRole() == Roles.ATTENDEE);
        
        int totalUsersNotified = 0;
        for (User user : users.get()) {

            UserNotification userNotif = new UserNotification();
            userNotif.setUserId(user.getId());
            userNotif.setNotifId(notif.getNotifId());
            userNotif.setRead(false);

            userNotifRepoGeneric.save(userNotif);
            totalUsersNotified++;

            emailService.sendEmail(
                    user.getEmail(),
                    "New Notification",
                    message
            );
        }

        log.info("[OK] NotifService.sendNotification() — Notification sent to {} users", totalUsersNotified);
        return Result.success(null);
    }

    @Override
    public Result<Object> sendNotificationForRegisterdUserAtEvent(List<Registration> registrations, String message) {
        log.info("[START] NotifService.sendNotificationForRegisterdUserAtEvent() — {} registrations", 
                registrations.size());

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

        log.info("[OK] NotifService.sendNotificationForRegisterdUserAtEvent() — Notification sent to {} users", 
                registrations.size());
        return Result.success(null);
    }
}