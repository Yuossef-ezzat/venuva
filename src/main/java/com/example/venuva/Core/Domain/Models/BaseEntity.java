package com.example.venuva.Core.Domain.Models;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // ===== Getter & Setter =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}