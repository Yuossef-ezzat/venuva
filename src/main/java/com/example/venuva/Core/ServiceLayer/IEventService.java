package com.example.venuva.Core.ServiceLayer;

import com.example.venuva.Shared.Dtos.EventDtos.*;

import java.util.List;
import java.util.Optional;

public interface IEventService {

    Optional<DetailedEventDto> getById(int id);

    List<AllEventsDto> getAll();

    int add(CreateEventDto dto);

    boolean update(int id, CreateEventDto dto);

    boolean delete(int id);
}
