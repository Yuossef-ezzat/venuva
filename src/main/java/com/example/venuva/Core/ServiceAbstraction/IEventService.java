package com.example.venuva.Core.ServiceAbstraction;



import java.util.List;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Shared.Dtos.EventDtos.*;


public interface IEventService {

    Result<DetailedEventDto> getById(Integer id);

    Result<List<AllEventsDto>> getAll();

    Result<Integer> add(CreateEventDto entity);

    Result<Boolean> update(DetailedEventDto entity);

    Result<Boolean> delete(Integer id);
}