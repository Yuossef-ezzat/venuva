package com.example.venuva.Infrastructure.PresistenceLayer.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.venuva.Core.Domain.Models.EventModule.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}