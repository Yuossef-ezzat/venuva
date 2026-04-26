package com.example.venuva.Core.ServiceAbstraction;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Shared.Dtos.RegisterationDto.*;

import java.math.BigDecimal;
import java.util.List;

public interface IRegistrationService {

    Result<RegistrationDto> registerUserToEvent(RegistrationRequestDto requestDto);

    Result<List<RegistrationDto>> getUserRegistrations(int userId);

    Result<Boolean> cancelRegistration(CancleRegisrationDto cancelRegistration);

    boolean isUserAlreadyRegistered(int userId, int eventId);
    Result<Integer> getNumberOfRegesters();
    Result<Integer> getNumberOfRegestersForEvent(int eventId);
    Result<BigDecimal> getTotalSpents(int userId);
}