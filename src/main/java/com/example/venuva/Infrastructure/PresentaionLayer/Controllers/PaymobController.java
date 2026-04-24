package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.venuva.Core.ServiceLayer.PayMobService;
import com.example.venuva.Shared.Dtos.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> pay(
            @RequestParam int amountCents,
            @RequestParam int userId) {
        try {
            log.info("PaymobController.pay() called with userId={}, amountCents={}", userId, amountCents);
            String iframeUrl = payMobService.payWithCard(amountCents, userId);
            log.info("PaymobController.pay() success: Payment URL generated for userId={}", userId);
            return ResponseEntity.ok(iframeUrl);
        } catch (Exception ex) {
            log.error("PaymobController.pay() failed: Payment initiation failed for userId={}, amount={}", 
                    userId, amountCents, ex);
            ErrorResponse error = new ErrorResponse(
                    "Payment initiation failed. Please try again later",
                    "PAYMENT_INIT_FAILED"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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
            log.info("PaymobController.callback() received PayMob notification: type={}", payload.type);
            boolean success = payMobService.paymobCallback(payload, hmacHeader);
            if (success) {
                log.info("PaymobController.callback() processed successfully");
            } else {
                log.warn("PaymobController.callback() payment was not successful or HMAC verification failed");
            }
            // Always return 200 to PayMob so it doesn't retry
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("PaymobController.callback() error processing PayMob callback", ex);
            // Still return 200 to prevent PayMob retry loops
            return ResponseEntity.ok().build();
        }
    }
}

