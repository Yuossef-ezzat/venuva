package com.example.venuva.Infrastructure.PresistenceLayer.Repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.venuva.Core.Domain.Models.PaymentModule.Payment;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    
    /**
     * Find a payment record by PayMob order ID
     * Used to correlate PayMob webhooks with local payment records
     */
    Optional<Payment> findByOrderId(int orderId);
}
