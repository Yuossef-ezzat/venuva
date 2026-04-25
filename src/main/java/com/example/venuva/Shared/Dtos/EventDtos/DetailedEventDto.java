package com.example.venuva.Shared.Dtos.EventDtos;

import com.example.venuva.Shared.Enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private int maxAttendance = 0;
    private EventStatus eventStatus;
    private boolean paymentRequired;
    private BigDecimal price;


    // Relations
    private int organizerId = 0;
    private String organizerName = null;
    private int categoryId = 0;
    private String categoryName = null;
}