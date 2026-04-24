package com.example.venuva.Core.ServiceLayer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class EventService implements IEventService {

    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final NotifService notifService;


    @Override
    public Result<Integer> add(CreateEventDto dto) {
        log.info("EventService.add() called with title='{}', organizerId={}, categoryId={}", 
                dto.getTitle(), dto.getOrganizerId(), dto.getCategoryId());

        if (dto.getDate().isBefore(LocalDateTime.now())) {
            log.warn("EventService.add() failed: Event date must be in the future");
            return Result.failure(new Error("Event date must be in the future"));
        }

        if (dto.getMaxAttendance() <= 0) {
            log.warn("EventService.add() failed: MaxAttendance must be greater than 0");
            return Result.failure(new Error("MaxAttendance must be greater than 0"));
        }

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

        log.info("EventService.add() success: Event created with id={}", Entity.getId());
        return Result.success(Entity.getId());
    }

    @Override
    public Result<Boolean> delete(Integer id) {
        log.info("EventService.delete() called with eventId={}", id);

        Event event = repository.findById(id).orElse(null);

        if (event == null) {
            log.warn("EventService.delete() failed: Event not found for id={}", id);
            return Result.failure(new Error("Event not found"));
        }

        repository.delete(event);
        boolean deleted = repository.findById(id).isEmpty();

        if (!deleted) {
            log.warn("EventService.delete() failed: Event with id={} could not be deleted", id);
            return Result.failure(new Error("Failed to delete event"));
        }

        String formattedDate = event.getDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

        notifService.sendNotification(
                "Event deleted: " + event.getTitle() +
                        " scheduled on " + formattedDate +
                        " at " + event.getLocation()
        );

        log.info("EventService.delete() success: Event id={} deleted", id);
        return Result.success(true);
    }

    @Override
    public Result<List<AllEventsDto>> getAll() {
        log.info("EventService.getAll() called");

        List<Event> events = repository.findAll();

        if (events == null || events.isEmpty()) {
            log.warn("EventService.getAll() failed: No events found");
            return Result.failure(new Error("No Events"));
        }

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

        log.info("EventService.getAll() success: {} events retrieved", dtos.size());
        return Result.success(dtos);
    }

    @Override
    public Result<DetailedEventDto> getById(Integer id) {
        log.info("EventService.getById() called with eventId={}", id);

        Event event = repository.findById(id).orElse(null);

        if (event == null) {
            log.warn("EventService.getById() failed: Event not found for id={}", id);
            return Result.failure(new Error("Event not found"));
        }

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

        log.info("EventService.getById() success: Event id={} retrieved", id);
        return Result.success(dto);
    }

    @Override
    public Result<Boolean> update(int id, DetailedEventDto dto) {
        log.info("EventService.update() called with eventId={}, title='{}'", id, dto.getTitle());

        if (dto == null) {
            log.warn("EventService.update() failed: Invalid event data (null)");
            return Result.failure(new Error("Invalid event data"));
        }

        if (dto.getDate().isBefore(LocalDateTime.now())) {
            log.warn("EventService.update() failed: Event date must be in the future");
            return Result.failure(new Error("Event date must be in the future"));
        }

        Event existingEvent = repository.findById(id)
                .orElse(null);

        if (existingEvent == null) {
            log.warn("EventService.update() failed: Event not found for id={}", id);
            return Result.failure(new Error("Event not found"));
        }

        User organizer = userRepository.findById(dto.getOrganizerId())
                .orElse(null);

        if (organizer == null) {
            log.warn("EventService.update() failed: User not found for organizerId={}", dto.getOrganizerId());
            return Result.failure(new Error("User not found"));
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElse(null);

        if (category == null) {
            log.warn("EventService.update() failed: Category not found for categoryId={}", dto.getCategoryId());
            return Result.failure(new Error("Category not found"));
        }

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

            log.info("EventService.update() success: Event id={} updated", id);
            return Result.success(true);

        } catch (Exception ex) {
            log.error("EventService.update() error: Failed to update event id={}", id, ex);
            return Result.failure(new Error("Failed to update event"));
        }
    }
}