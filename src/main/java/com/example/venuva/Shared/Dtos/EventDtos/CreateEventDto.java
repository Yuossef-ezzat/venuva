package com.example.venuva.Shared.Dtos.EventDtos;

import com.example.venuva.Shared.Enums.EventStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateEventDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Date is required")
    @Future(message = "Date must be in the future")
    private LocalDateTime date;

    @NotBlank(message = "Location is required")
    private String location;

    @Min(value = 1, message = "Max attendance must be at least 1")
    private int maxAttendance;

    @NotNull(message = "Event status is required")
    private EventStatus eventStatus = EventStatus.SCHEDULED;

    @JsonProperty("paymentRequired")
    private boolean paymentRequired;

    @NotNull(message = "Organizer ID is required")
    private int organizerId;

    @NotNull(message = "Category ID is required")
    private int categoryId;

    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price = BigDecimal.ZERO;
}
