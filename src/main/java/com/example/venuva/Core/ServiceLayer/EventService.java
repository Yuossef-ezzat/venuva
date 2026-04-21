package com.example.venuva.Core.ServiceLayer;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Core.Domain.Models.EventModule.Category;
import com.example.venuva.Core.Domain.Models.EventModule.Event;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Core.ServiceAbstraction.IEventService;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.CategoryRepository;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.EventRepository;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.UserRepository;
import com.example.venuva.Shared.Dtos.EventDtos.AllEventsDto;
import com.example.venuva.Shared.Dtos.EventDtos.CreateEventDto;
import com.example.venuva.Shared.Dtos.EventDtos.DetailedEventDto;
import com.example.venuva.Core.Domain.Abstractions.Error;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final NotifService notifService;


    @Override
    public Result<Integer> add(CreateEventDto dto) {

        if (dto.getDate().isBefore(LocalDateTime.now()))
            return Result.failure(new Error("Event date must be in the future"));

        if (dto.getMaxAttendance() <= 0)
            return Result.failure(new Error("MaxAttendance must be greater than 0"));

        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));

        User organizer = userRepository.findById(dto.getOrganizerId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setLocation(dto.getLocation());
        event.setCategory(category);
        event.setOrganizer(organizer);
        event.setMaxAttendance(dto.getMaxAttendance());
        event.setEventStatus(dto.getEventStatus());
        event.setPaymentRequired(dto.isPaymentRequired());

        var Entity = repository.save(event);

        String formattedDate = event.getDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

        notifService.sendNotification(
                "New event created: " + event.getTitle() +
                        " on " + formattedDate +
                        " at " + event.getLocation()
        );

        return Result.success(Entity.getId());
    }

    @Override
    public Result<Boolean> delete(Integer id) {

        Event event = repository.findById(id).orElse(null);

        if (event == null)
            return Result.failure(new Error("Event not found"));

        repository.delete(event);
        boolean deleted = repository.findById(id).isEmpty();

        if (!deleted)
            return Result.failure(new Error("Failed to delete event"));

        String formattedDate = event.getDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

        notifService.sendNotification(
                "Event deleted: " + event.getTitle() +
                        " scheduled on " + formattedDate +
                        " at " + event.getLocation()
        );

        return Result.success(true);
    }

    @Override
    public Result<List<AllEventsDto>> getAll() {

        List<Event> events = repository.findAll();

        if (events == null || events.isEmpty())
            return Result.failure(new Error("No Events"));

        List<AllEventsDto> dtos = events.stream().map(e -> {

            AllEventsDto dto = new AllEventsDto();

            dto.setId(e.getId());
            dto.setTitle(e.getTitle());
            dto.setDate(e.getDate());
            dto.setLocation(e.getLocation());

            dto.setOrganizerId(e.getOrganizer().getId());
            dto.setOrganizerName(e.getOrganizer().getUsername());

            dto.setCategoryId(e.getCategory().getId());
            dto.setCategoryName(e.getCategory().getName());

            dto.setEventStatus(e.getEventStatus());
            dto.setPaymentRequired(e.isPaymentRequired());
            dto.setMaxAttendance(e.getMaxAttendance());

            return dto;

        }).collect(Collectors.toList());

        return Result.success(dtos);
    }

    @Override
    public Result<DetailedEventDto> getById(Integer id) {

        Event event = repository.findById(id).orElse(null);

        if (event == null)
            return Result.failure(new Error("Event not found"));

        DetailedEventDto dto = new DetailedEventDto();

        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setLocation(event.getLocation());

        dto.setOrganizerId(event.getOrganizer() != null ? event.getOrganizer().getId() : null);
        dto.setOrganizerName(
                event.getOrganizer() != null ? event.getOrganizer().getUsername() : null
        );

        dto.setCategoryId(event.getCategory() != null ? event.getCategory().getId() : null);
        dto.setCategoryName(
                event.getCategory() != null ? event.getCategory().getName() : null
        );

        dto.setEventStatus(event.getEventStatus());
        dto.setPaymentRequired(event.isPaymentRequired());
        dto.setMaxAttendance(event.getMaxAttendance());

        dto.setRegistrations(
            event.getRegistrations().stream()
                    .map(r -> String.valueOf(r.getId()))
                    .toList()
        );

        dto.setPayments(
            event.getPayments().stream()
                    .map(p -> String.valueOf(p.getId()))
                    .toList()
        );

        return Result.success(dto);
    }

    @Override
    public Result<Boolean> update(int id, DetailedEventDto dto) {

        if (dto == null)
            return Result.failure(new Error("Invalid event data"));

        if (dto.getDate().isBefore(LocalDateTime.now()))
            return Result.failure(new Error("Event date must be in the future"));

        Event existingEvent = repository.findById(id)
                .orElse(null);

        if (existingEvent == null)
            return Result.failure(new Error("Event not found"));

        User organizer = userRepository.findById(dto.getOrganizerId())
                .orElse(null);

        if (organizer == null)
            return Result.failure(new Error("User not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElse(null);

        if (category == null)
            return Result.failure(new Error("Category not found"));

        // ===== mapping =====
        existingEvent.setTitle(dto.getTitle());
        existingEvent.setDescription(dto.getDescription());
        existingEvent.setDate(dto.getDate());
        existingEvent.setLocation(dto.getLocation());

        existingEvent.setOrganizer(organizer);
        existingEvent.setCategory(category);

        existingEvent.setEventStatus(dto.getEventStatus());
        existingEvent.setPaymentRequired(dto.isPaymentRequired());
        existingEvent.setMaxAttendance(dto.getMaxAttendance());

        try {
            repository.save(existingEvent);

            String formattedDate = existingEvent.getDate()
                    .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

            notifService.sendNotificationForRegisterdUserAtEvent(
                    existingEvent.getRegistrations(),
                    "Event updated: " + existingEvent.getTitle()
                            + " now scheduled on " + formattedDate
                            + " at " + existingEvent.getLocation()
            );

            return Result.success(true);

        } catch (Exception ex) {
            return Result.failure(new Error("Failed to update event"));
        }
    }
}