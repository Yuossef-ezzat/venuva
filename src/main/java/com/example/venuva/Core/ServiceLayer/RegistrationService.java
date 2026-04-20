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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

        if (isUserAlreadyRegistered(requestDto.getUserId(), requestDto.getEventId())) {
            return Result.failure(
                    new Error( "User already registered for this event")
            );
        }
        Event event = eventRepository.findById(requestDto.getEventId())
                .orElse(null);

        if (event == null) {
            return Result.failure(
                    new Error( "Event not found")
            );
        }
        User user = userRepository.findById(requestDto.getUserId())
                .orElse(null);

        if (user == null) {
            return Result.failure(
                    new Error( "User not found")
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

        return Result.success(dto);
    }


    @Override
    public Result<Boolean> cancelRegistration(CancleRegisrationDto dto) {

        if (dto == null) {
            return Result.failure(new Error("Request body is missing"));
        }
        Registration registration = repository.findByEventIdAndUserId(dto.getEventId(), dto.getUserId());

        if (registration == null) {
            return Result.failure(
                    new Error("User is not registered for this event")
            );
        }
        repository.delete(registration);
        return Result.success(true);
    }

    // =======================
    // Get user registrations
    // =======================
    @Override
    public Result<List<RegistrationDto>> getUserRegistrations(int userId) {

        List<Registration> registrations = repository.findByUserId(userId);

        if (registrations == null || registrations.isEmpty()) {
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

        return Result.success(result);
    }
}