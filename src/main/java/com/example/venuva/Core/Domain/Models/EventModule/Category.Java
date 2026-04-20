package com.example.venuva.Core.Domain.Models.EventModule;

import jakarta.persistence.*;
import com.example.venuva.Core.Domain.Models.BaseEntity;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Event> events;

    // ===== Getters & Setters =====

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}