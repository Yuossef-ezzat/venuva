package com.example.venuva.Shared.Dtos.EventDtos;

import com.example.venuva.Shared.Enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailedEventDto {

    private int id;
    private String title;
    private String description;
    private LocalDateTime date;
    private String location;
    private int maxAttendance;
    private EventStatus eventStatus;
    private boolean paymentRequired;

    // Relations
    private int organizerId;
    private String organizerName;
    private int categoryId;
    private String categoryName;

    private List<String> registrations;
    private List<String> payments;
    private List<String> notifications;
}