package com.example.venuva.Core.ServiceLayer;


import com.example.venuva.Core.Domain.Abstractions.*;
import com.example.venuva.Core.Domain.Abstractions.Error;
import com.example.venuva.Core.Domain.Models.EventModule.Event;
import com.example.venuva.Core.Domain.Models.RegistrationModules.Registration;
import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Core.ServiceAbstraction.IRegistrationService;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.*;
import com.example.venuva.Shared.Dtos.RegisterationDto.CancleRegisrationDto;
import com.example.venuva.Shared.Dtos.RegisterationDto.RegistrationDto;
import com.example.venuva.Shared.Dtos.RegisterationDto.RegistrationRequestDto;
import com.example.venuva.Shared.Enums.RegistrationStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService implements IRegistrationService {

    private final RegistrationRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    public boolean isUserAlreadyRegistered(int userId, int eventId) {
        return repository.existsByUserIdAndEventId(userId, eventId);
    }

    @Override
    public Result<RegistrationDto> registerUserToEvent(RegistrationRequestDto requestDto) {
        log.info("RegistrationService.registerUserToEvent() called with userId={}, eventId={}", 
                requestDto.getUserId(), requestDto.getEventId());

        if (isUserAlreadyRegistered(requestDto.getUserId(), requestDto.getEventId())) {
            log.warn("RegistrationService.registerUserToEvent() failed: User {} already registered for event {}", 
                    requestDto.getUserId(), requestDto.getEventId());
            return Result.failure(
                    new Error("User already registered for this event")
            );
        }
        Event event = eventRepository.findById(requestDto.getEventId())
                .orElse(null);

        if (event == null) {
            log.warn("RegistrationService.registerUserToEvent() failed: Event not found with id={}", 
                    requestDto.getEventId());
            return Result.failure(
                    new Error("Event not found")
            );
        }
        User user = userRepository.findById(requestDto.getUserId())
                .orElse(null);

        if (user == null) {
            log.warn("RegistrationService.registerUserToEvent() failed: User not found with id={}", 
                    requestDto.getUserId());
            return Result.failure(
                    new Error("User not found")
            );
        }

        Registration registration = new Registration();
        
        registration.setUser(user);
        registration.setEvent(event);

        registration.setRegistrationStatus(event.isPaymentRequired()
                ? RegistrationStatus.PENDING
                : RegistrationStatus.PAID);

        repository.save(registration);

        RegistrationDto dto = new RegistrationDto();
        dto.setRegistrationId(registration.getId());
        dto.setUserId(user.getId());
        dto.setEventId(event.getId());
        dto.setEventTitle(event.getTitle());
        dto.setEventDate(event.getDate());
        dto.setEventLocation(event.getLocation());
        dto.setPaymentRequired(event.isPaymentRequired());
        dto.setStatus(registration.getRegistrationStatus().toString());

        log.info("RegistrationService.registerUserToEvent() success: User {} registered to event {}", 
                requestDto.getUserId(), requestDto.getEventId());
        return Result.success(dto);
    }


    @Override
    public Result<Boolean> cancelRegistration(CancleRegisrationDto dto) {
        log.info("RegistrationService.cancelRegistration() called with userId={}, eventId={}", 
                dto.getUserId(), dto.getEventId());

        if (dto == null) {
            log.warn("RegistrationService.cancelRegistration() failed: Request body is missing");
            return Result.failure(new Error("Request body is missing"));
        }
        Registration registration = repository.findByEventIdAndUserId(dto.getEventId(), dto.getUserId());

        if (registration == null) {
            log.warn("RegistrationService.cancelRegistration() failed: User {} not registered for event {}", 
                    dto.getUserId(), dto.getEventId());
            return Result.failure(
                    new Error("User is not registered for this event")
            );
        }
        repository.delete(registration);
        log.info("RegistrationService.cancelRegistration() success: User {} unregistered from event {}", 
                dto.getUserId(), dto.getEventId());
        return Result.success(true);
    }

    // =======================
    // Get user registrations
    // =======================
    @Override
    public Result<List<RegistrationDto>> getUserRegistrations(int userId) {
        log.info("RegistrationService.getUserRegistrations() called with userId={}", userId);

        List<Registration> registrations = repository.findByUserId(userId);

        if (registrations == null || registrations.isEmpty()) {
            log.warn("RegistrationService.getUserRegistrations() failed: No registrations found for userId={}", userId);
            return Result.failure(
                    new Error("No registrations found")
            );
        }

        List<RegistrationDto> result = registrations.stream().map(r -> {

            Event event = eventRepository.findById(r.getEventId()).orElse(null);

            RegistrationDto dto = new RegistrationDto();
            dto.setRegistrationId(r.getId());
            dto.setUserId(r.getUserId());
            dto.setEventId(r.getEventId());

            if (event != null) {
                dto.setEventTitle(event.getTitle());
                dto.setEventDate(event.getDate());
                dto.setEventLocation(event.getLocation());
                dto.setPaymentRequired(event.isPaymentRequired());
            }

            dto.setStatus(r.getRegistrationStatus().toString());

            return dto;

        }).collect(Collectors.toList());

        log.info("RegistrationService.getUserRegistrations() success: {} registrations retrieved for userId={}", 
                result.size(), userId);
        return Result.success(result);
    }
}