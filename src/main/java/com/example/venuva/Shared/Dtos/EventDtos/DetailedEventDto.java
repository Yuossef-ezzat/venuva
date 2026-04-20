package com.example.venuva.Shared.Dtos.EventDtos;

import com.example.venuva.Shared.Enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailedEventDto {

    private int id;
    private String title;
    private String description;
    private LocalDateTime date;
    private String location;
    private int maxAttendance;
    private EventStatus eventStatus;
    private boolean paymentRequired;

    // Relations (flat)
    private int organizerId;
    private String organizerEmail;
    private int categoryId;
    private String categoryName;

    // Counts instead of full nested lists (avoids over-fetching)
    private int registrationsCount;
    private int paymentsCount;
}
