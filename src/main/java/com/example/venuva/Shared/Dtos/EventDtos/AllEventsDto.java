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
public class AllEventsDto {

    private int id;
    private String title;
    private LocalDateTime date;
    private String location;
    private int maxAttendance;
    private EventStatus eventStatus;
    private boolean paymentRequired;

    // Relations (flat - no nested objects)
    private int organizerId;
    private String organizerName;
    private int categoryId;
    private String categoryName;
}
