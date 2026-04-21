package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Core.ServiceAbstraction.INotifService;
import com.example.venuva.Shared.Dtos.Notifs.NotifDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final INotifService notifService;

    // GET /api/notifications/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<Result<List<NotifDTO>>> getNotifs(@PathVariable int userId) {
        Result<List<NotifDTO>> result = notifService.getNotifsById(userId);
        return result.isSuccess()
                ? ResponseEntity.ok(result)
                : ResponseEntity.badRequest().body(result);
    }

    // PUT /api/notifications/mark-read/{notifId}
    @PutMapping("/mark-read/{notifId}")
    public ResponseEntity<Result<NotifDTO>> markAsRead(@PathVariable int notifId) {
        Result<NotifDTO> result = notifService.markNotifAsRead(notifId);
        return result.isSuccess()
                ? ResponseEntity.ok(result)
                : ResponseEntity.badRequest().body(result);
    }
}