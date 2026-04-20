package com.example.venuva.Shared.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotifDTO {

    private int notifId;

    private String message;

    private LocalDateTime date = LocalDateTime.now();

    private String userId;

    private String userName;

    private boolean isRead = false;
}