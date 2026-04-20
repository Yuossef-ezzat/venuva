package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Core.ServiceAbstraction.IEventService;
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

    @GetMapping
    public ResponseEntity<Result<List<AllEventsDto>>> getAll() {

        Result<List<AllEventsDto>> result = eventService.getAll();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<DetailedEventDto>> getById(@PathVariable int id) {

        Result<DetailedEventDto> result = eventService.getById(id);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Result<Integer>> create(@Valid @RequestBody CreateEventDto dto) {

        Result<Integer> result = eventService.add(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Result<Boolean>> update(
            @PathVariable int id,
            @Valid @RequestBody DetailedEventDto dto) {

        Result<Boolean> result = eventService.update(id, dto);

        return ResponseEntity.ok(result);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<Boolean>> delete(@PathVariable int id) {

        Result<Boolean> result = eventService.delete(id);

        return ResponseEntity.ok(result);
    }
}