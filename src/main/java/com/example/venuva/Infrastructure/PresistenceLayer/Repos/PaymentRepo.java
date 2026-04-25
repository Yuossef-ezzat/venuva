package com.example.venuva.Infrastructure.PresistenceLayer.Repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.venuva.Core.Domain.Models.PaymentModule.Payment;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    
}
