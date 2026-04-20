package com.example.venuva.Core.ServiceLayer;

import com.example.venuva.Core.Domain.Models.EventModule.*;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Infrastructure.PresistenceLayer.Data.Repos.*;
import com.example.venuva.Infrastructure.PresistenceLayer.Data.Repos.UserRepository;
import com.example.venuva.Shared.Dtos.EventDtos.AllEventsDto;
import com.example.venuva.Shared.Dtos.EventDtos.CreateEventDto;
import com.example.venuva.Shared.Dtos.EventDtos.DetailedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    // Inject CategoryRepository when available
    // private final CategoryRepository categoryRepository;

    // ===== GET BY ID =====

    @Override
    public Optional<DetailedEventDto> getById(int id) {
        return eventRepository.findById(id)
                .map(this::mapToDetailedDto);
    }

    // ===== GET ALL =====

    @Override
    public List<AllEventsDto> getAll() {
        return eventRepository.findAllWithDetails()
                .stream()
                .map(this::mapToAllEventsDto)
                .collect(Collectors.toList());
    }

    // ===== ADD =====

    @Override
    public int add(CreateEventDto dto) {

        User organizer = userRepository.findById(dto.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found with id: " + dto.getOrganizerId()));

        // Fetch category when CategoryRepository is injected:
        // Category category = categoryRepository.findById(dto.getCategoryId())
        //         .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));

        Category category = new Category();
        category.setId(dto.getCategoryId()); // Use proxy reference until CategoryRepository is injected

        Event newEvent = new Event();
        newEvent.setTitle(dto.getTitle());
        newEvent.setDescription(dto.getDescription());
        newEvent.setDate(dto.getDate());
        newEvent.setLocation(dto.getLocation());
        newEvent.setMaxAttendance(dto.getMaxAttendance());
        newEvent.setEventStatus(dto.getEventStatus());
        newEvent.setPaymentRequired(dto.isPaymentRequired());
        newEvent.setOrganizer(organizer);
        newEvent.setCategory(category);

        Event saved = eventRepository.save(newEvent);
        return saved.getId();
    }

    // ===== UPDATE =====

    @Override
    public boolean update(int id, CreateEventDto dto) {

        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setDate(dto.getDate());
        existing.setLocation(dto.getLocation());
        existing.setMaxAttendance(dto.getMaxAttendance());
        existing.setEventStatus(dto.getEventStatus());
        existing.setPaymentRequired(dto.isPaymentRequired());

        // Update category reference if changed
        if (existing.getCategory().getId() != dto.getCategoryId()) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            existing.setCategory(category);
        }

        eventRepository.save(existing);
        return true;
    }

    // ===== DELETE =====

    @Override
    public boolean delete(int id) {
        if (!eventRepository.existsById(id)) {
            return false;
        }
        eventRepository.deleteById(id);
        return true;
    }

    // ===== MAPPING HELPERS =====

    private AllEventsDto mapToAllEventsDto(Event event) {
        return new AllEventsDto(
                event.getId(),
                event.getTitle(),
                event.getDate(),
                event.getLocation(),
                event.getMaxAttendance(),
                event.getEventStatus(),
                event.isPaymentRequired(),
                event.getOrganizer().getId(),
                event.getOrganizer().getUsername(),
                event.getCategory().getId(),
                event.getCategory().getName()
        );
    }

    private DetailedEventDto mapToDetailedDto(Event event) {
        DetailedEventDto dto = new DetailedEventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setLocation(event.getLocation());
        dto.setMaxAttendance(event.getMaxAttendance());
        dto.setEventStatus(event.getEventStatus());
        dto.setPaymentRequired(event.isPaymentRequired());
        dto.setOrganizerId(event.getOrganizer().getId());
        dto.setOrganizerEmail(event.getOrganizer().getEmail());
        dto.setCategoryId(event.getCategory().getId());
        dto.setCategoryName(event.getCategory().getName());
        dto.setRegistrationsCount(
                event.getRegistrations() != null ? event.getRegistrations().size() : 0
        );
        dto.setPaymentsCount(
                event.getPayments() != null ? event.getPayments().size() : 0
        );
        return dto;
    }
}
