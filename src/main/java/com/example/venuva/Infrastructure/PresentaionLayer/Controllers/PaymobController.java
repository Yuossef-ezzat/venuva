package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.ServiceLayer.PayMobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymobController {

    private final PayMobService payMobService;

    // ===== POST /api/payments/pay =====
    // Initiate a card payment — returns a PayMob iFrame URL
    // userId is passed as a query param (or extracted from JWT in production)
    @PostMapping("/pay")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<String> pay(
            @RequestParam int amountCents,
            @RequestParam int userId) {
        try {
            String iframeUrl = payMobService.payWithCard(amountCents, userId);
            return ResponseEntity.ok(iframeUrl);
        } catch (Exception ex) {
            log.error("Payment initiation failed for userId={}, amount={}", userId, amountCents, ex);
            return ResponseEntity.internalServerError()
                    .body("Payment initiation failed: " + ex.getMessage());
        }
    }

    // ===== POST /api/payments/callback =====
    // PayMob webhook — receives transaction result and verifies HMAC
    // Must be public (no auth) so PayMob servers can call it
    @PostMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestBody PayMobService.PaymobCallbackPayload payload,
            @RequestParam(value = "hmac", required = false) String hmacHeader) {
        try {
            log.info("Received PayMob callback: type={}", payload.type);
            boolean success = payMobService.paymobCallback(payload, hmacHeader);
            if (success) {
                log.info("PayMob callback processed successfully");
            } else {
                log.warn("PayMob callback: payment was not successful or HMAC failed");
            }
            // Always return 200 to PayMob so it doesn't retry
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error processing PayMob callback", ex);
            // Still return 200 to prevent PayMob retry loops
            return ResponseEntity.ok().build();
        }
    }
}
