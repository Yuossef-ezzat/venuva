package com.example.venuva.Infrastructure.PresistenceLayer.Repos;

import com.example.venuva.Core.Domain.Models.EventModule.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByCategoryId(int categoryId);

    List<Event> findByOrganizerId(int organizerId);

    @Query("SELECT e FROM Event e JOIN FETCH e.organizer JOIN FETCH e.category")
    List<Event> findAllWithDetails();
}
