package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import com.example.aopmodule.aop.src.main.java.com.example.AOP.Annotation.HandleException;
import com.example.venuva.Core.ServiceLayer.PayMobService;
import com.example.venuva.Shared.Dtos.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Paymob")
@RequiredArgsConstructor
@Slf4j
public class PaymobController {

    private final PayMobService payMobService;

    // ===== POST /api/Paymob/pay =====
    // Initiate a card payment — returns a PayMob iFrame URL
    // userId is passed as a query param (or extracted from JWT in production)
    @PostMapping("/pay")
    @PreAuthorize("hasRole('ATTENDEE') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    @HandleException

    public ResponseEntity<?> pay(
            @RequestParam(required = false) Integer amount,
            @RequestParam(required = false) Integer amountCents,
            @RequestParam int userId,
            @RequestParam int eventId) {
        try {
            // Accept either `amount` (EGP) or `amountCents` (smallest unit). Prefer `amount` when provided.
            int amountToUse;
            if (amount != null) {
                amountToUse = amount;
            } else if (amountCents != null) {
                // convert piasters to EGP
                amountToUse = amountCents / 100;
            } else {
                throw new IllegalArgumentException("Required request parameter 'amount' or 'amountCents' is missing");
            }

            log.info("PaymobController.pay() called with userId={}, amount(EGP)={}", userId, amountToUse);
            String iframeUrl = payMobService.payWithCard(amountToUse, userId, eventId);
            return ResponseEntity.ok(iframeUrl);
        } catch (Exception ex) {
            // ✅ اطبع الـ full exception message
            log.error("PAYMENT FAILED - Cause: {}", ex.getMessage(), ex);
            ErrorResponse error = new ErrorResponse(
                    "Payment failed: " + ex.getMessage(), // مؤقتاً عشان تشوف السبب
                    "PAYMENT_INIT_FAILED"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ===== POST /api/Paymob/callback =====
    // PayMob webhook — receives transaction result and verifies HMAC
    // Must be public (no auth) so PayMob servers can call it
    @PostMapping("/callback")
    @HandleException
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

