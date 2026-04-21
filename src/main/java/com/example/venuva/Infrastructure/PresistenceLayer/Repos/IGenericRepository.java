package com.example.venuva.Infrastructure.PresistenceLayer.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.List;

public interface IGenericRepository<T, ID> extends JpaRepository<T, ID> {
    // Option 1: Use Spring's native query methods (recommended)
    List<T> findAll();
    default Optional<List<T>> findAll(Predicate<T> predicate) {
        return Optional.of(findAll().stream()
                .filter(predicate)
                .collect(Collectors.toList()));
    }
    
    Optional<T> findById(ID id);
    
    default Optional<T> findOne(Predicate<T> predicate) {
        return findAll().stream()
                .filter(predicate)
                .findFirst();
    }
}