package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.ServiceLayer.IEventService;
import com.example.venuva.Shared.Dtos.EventDtos.AllEventsDto;
import com.example.venuva.Shared.Dtos.EventDtos.CreateEventDto;
import com.example.venuva.Shared.Dtos.EventDtos.DetailedEventDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final IEventService eventService;

    // ===== GET /api/events =====
    // Get all events (public)
    @GetMapping
    public ResponseEntity<List<AllEventsDto>> getAll() {
        List<AllEventsDto> events = eventService.getAll();
        return ResponseEntity.ok(events);
    }

    // ===== GET /api/events/{id} =====
    // Get event details by ID (public)
    @GetMapping("/{id}")
    public ResponseEntity<DetailedEventDto> getById(@PathVariable int id) {
        return eventService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== POST /api/events =====
    // Create a new event (organizer/admin only)
    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> create(@Valid @RequestBody CreateEventDto dto) {
        int newEventId = eventService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEventId);
    }

    // ===== PUT /api/events/{id} =====
    // Update an existing event (organizer/admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Void> update(
            @PathVariable int id,
            @Valid @RequestBody CreateEventDto dto) {
        boolean updated = eventService.update(id, dto);
        return updated
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ===== DELETE /api/events/{id} =====
    // Delete an event (admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean deleted = eventService.delete(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
