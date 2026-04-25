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

    private Integer id = null;
    private String title = null;
    private String description = null;
    private LocalDateTime date = null;
    private String location = null;
    private Integer maxAttendance = null;
    private EventStatus eventStatus;
    private Boolean paymentRequired = null;
    private BigDecimal price = null;


    // Relations
    private Integer organizerId = null;
    private String organizerName = null;
    private Integer categoryId = null;
    private String categoryName = null;
}