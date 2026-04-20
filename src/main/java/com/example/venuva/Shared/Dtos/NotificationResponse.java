package com.example.venuva.Shared.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NotificationResponse {

    public NotificationResponse(int id, String message, LocalDateTime date) {
        super();
        this.id = id;
        this.message = message;
        this.date = date;
    }
    private int id;
    private String message;
    private LocalDateTime date;

    // User info (flat)
    private int userId;
    private String username;

    // Event info (flat)
    private int eventId;
    private String eventTitle;
}
