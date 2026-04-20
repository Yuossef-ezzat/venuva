package com.example.venuva.Core.ServiceLayer;

import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.UserRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayMobService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    private static final String BASE_URL = "https://accept.paymob.com/api/";

    // Read from environment variables (same approach as .NET version)
    private final String apiKey = System.getenv("PAYMOB_API_KEY") != null
            ? System.getenv("PAYMOB_API_KEY")
            : "ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SndjbTltYVd4bFgzQnJJam94TVRNd01UZ3NJbU5zWVhOeklqb2lUV1Z5WTJoaGJuUWlMQ0p1WVcxbElqb2lhVzVwZEdsaGJDSjkuazJvdExPbXNZajFQM1FFNTBfeU1mWVBZS0U3S3VuTEpKMThRRkgzR1V0a3dXZG5wNG5kQlc3eDc1WGpOcHNyUTV0ckVPRzZlX2VkdG9jVjJDcHpzc2c=";

    private final int integrationId = System.getenv("PAYMOB_INTEGRATION_ID") != null
            ? Integer.parseInt(System.getenv("PAYMOB_INTEGRATION_ID"))
            : 4896849;

    private final int iframeId = System.getenv("PAYMOB_IFRAME_ID") != null
            ? Integer.parseInt(System.getenv("PAYMOB_IFRAME_ID"))
            : 897502;

    private final String hmacSecretKey = System.getenv("HMAC_SECRET_KEY") != null
            ? System.getenv("HMAC_SECRET_KEY")
            : "0DC8EF3D0DAAB2C53EC6DDD2BEA8EDD0";

    // ===== STEP 1: AUTHENTICATE → GET TOKEN =====

    public String authenticate() {
        log.info("Starting PayMob authentication...");

        Map<String, String> body = new HashMap<>();
        body.put("api_key", apiKey);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                BASE_URL + "auth/tokens",
                body,
                AuthResponse.class
        );

        if (response.getBody() == null || response.getBody().token == null) {
            log.error("PayMob returned null or empty token");
            throw new RuntimeException("PayMob authentication failed: empty token");
        }

        log.info("PayMob authentication successful. Token length: {}", response.getBody().token.length());
        return response.getBody().token;
    }

    // ===== STEP 2: CREATE ORDER =====

    public int createOrder(String token, int amountCents) {
        log.info("Creating PayMob order for amount: {} cents", amountCents);

        Map<String, Object> body = new HashMap<>();
        body.put("auth_token", token);
        body.put("delivery_needed", false);
        body.put("amount_cents", amountCents * 100);
        body.put("currency", "EGP");
        body.put("items", new Object[0]);

        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(
                BASE_URL + "ecommerce/orders",
                body,
                OrderResponse.class
        );

        if (response.getBody() == null || response.getBody().id == 0) {
            log.error("PayMob returned invalid order ID");
            throw new RuntimeException("PayMob order creation failed: invalid order ID");
        }

        log.info("Order created successfully. Order ID: {}", response.getBody().id);
        return response.getBody().id;
    }

    // ===== STEP 3: GET PAYMENT KEY =====

    public String getPaymentKey(String token, int orderId, int amountCents, int userId) {
        log.info("Getting payment key for order: {}, amount: {}", orderId, amountCents);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Map<String, Object> billingData = new HashMap<>();
        billingData.put("apartment", String.valueOf(userId));
        billingData.put("email", user.getEmail());
        billingData.put("floor", "NA");
        billingData.put("first_name", user.getUsername());
        billingData.put("street", "NA");
        billingData.put("building", "NA");
        billingData.put("phone_number", "01000000000");
        billingData.put("shipping_method", "NA");
        billingData.put("postal_code", "NA");
        billingData.put("city", "Cairo");
        billingData.put("country", "EG");
        billingData.put("last_name", "NA");
        billingData.put("state", "Cairo");

        Map<String, Object> body = new HashMap<>();
        body.put("auth_token", token);
        body.put("amount_cents", amountCents * 100);
        body.put("expiration", 3600);
        body.put("order_id", orderId);
        body.put("billing_data", billingData);
        body.put("currency", "EGP");
        body.put("integration_id", integrationId);

        ResponseEntity<PaymentKeyResponse> response = restTemplate.postForEntity(
                BASE_URL + "acceptance/payment_keys",
                body,
                PaymentKeyResponse.class
        );

        if (response.getBody() == null || response.getBody().token == null) {
            log.error("PayMob returned invalid payment key");
            throw new RuntimeException("PayMob payment key generation failed");
        }

        log.info("Payment key generated successfully. Key length: {}", response.getBody().token.length());
        return response.getBody().token;
    }

    // ===== STEP 4: BUILD IFRAME URL =====

    public String getIframeUrl(String paymentKey) {
        String url = String.format(
                "https://accept.paymob.com/api/acceptance/iframes/%d?payment_token=%s",
                iframeId, paymentKey
        );
        log.info("Generated iframe URL with IFrame ID: {}", iframeId);
        return url;
    }

    // ===== FULL PAYMENT FLOW =====

    public String payWithCard(int amountCents, int userId) {
        log.info("=== Starting payment process for {} cents ===", amountCents);

        String token = authenticate();
        log.info("Step 1/3: Authentication completed");

        int orderId = createOrder(token, amountCents);
        log.info("Step 2/3: Order created with ID: {}", orderId);

        String paymentKey = getPaymentKey(token, orderId, amountCents, userId);
        log.info("Step 3/3: Payment key generated");

        String iframeUrl = getIframeUrl(paymentKey);
        log.info("=== Payment process completed. IFrame URL: {} ===", iframeUrl);

        return iframeUrl;
    }

    // ===== CALLBACK VERIFICATION =====

    public boolean paymobCallback(PaymobCallbackPayload payload, String hmacHeader) {
        try {
            PaymobObj obj = payload.obj;

            if (obj.data != null && obj.data.message != null) {
                log.warn("PayMob Error Message: {}", obj.data.message);
                log.warn("TXN Response Code: {}", obj.data.txnResponseCode);
            }

            // Build data string for HMAC verification (same order as .NET version)
            String dataString = obj.amountCents
                    + obj.createdAt
                    + obj.currency
                    + String.valueOf(obj.errorOccurred).toLowerCase()
                    + String.valueOf(obj.hasParentTransaction).toLowerCase()
                    + obj.id
                    + obj.integrationId
                    + String.valueOf(obj.is3dSecure).toLowerCase()
                    + String.valueOf(obj.isAuth).toLowerCase()
                    + String.valueOf(obj.isCapture).toLowerCase()
                    + String.valueOf(obj.isRefunded).toLowerCase()
                    + String.valueOf(obj.isStandalonePayment).toLowerCase()
                    + String.valueOf(obj.isVoided).toLowerCase()
                    + obj.order.id
                    + obj.owner
                    + String.valueOf(obj.pending).toLowerCase()
                    + obj.sourceData.pan
                    + obj.sourceData.subType
                    + obj.sourceData.type
                    + String.valueOf(obj.success).toLowerCase();

            // Compute HMAC-SHA512
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(
                    hmacSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512"
            );
            hmac.init(keySpec);
            byte[] hashBytes = hmac.doFinal(dataString.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String calculatedHmac = sb.toString();

            if (!calculatedHmac.equalsIgnoreCase(hmacHeader)) {
                log.warn("HMAC verification failed. Expected: {}, Got: {}", calculatedHmac, hmacHeader);
                return false;
            }

            log.info("HMAC verified successfully. Payment success: {}", obj.success);

            // TODO: Save Payment to DB using PaymentRepository
            // Payment payment = new Payment();
            // payment.setAmount(BigDecimal.valueOf(obj.amountCents).divide(BigDecimal.valueOf(100)));
            // payment.setPaymentStatus(obj.success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
            // payment.setTransactionDate(LocalDateTime.now());
            // paymentRepository.save(payment);

            return Boolean.TRUE.equals(obj.success);

        } catch (Exception ex) {
            log.error("Error processing PayMob callback", ex);
            return false;
        }
    }

    // ===== INTERNAL RESPONSE DTOs =====

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AuthResponse {
        public String token;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class OrderResponse {
        public int id;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PaymentKeyResponse {
        public String token;
    }

    // ===== CALLBACK PAYLOAD DTOs =====

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymobCallbackPayload {
        public String type;
        public PaymobObj obj;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymobObj {
        public Long id;
        public Boolean pending;

        @JsonProperty("amount_cents")
        public Integer amountCents;

        public Boolean success;

        @JsonProperty("is_auth")
        public Boolean isAuth;

        @JsonProperty("is_capture")
        public Boolean isCapture;

        @JsonProperty("is_standalone_payment")
        public Boolean isStandalonePayment;

        @JsonProperty("is_voided")
        public Boolean isVoided;

        @JsonProperty("is_refunded")
        public Boolean isRefunded;

        @JsonProperty("is_3d_secure")
        public Boolean is3dSecure;

        @JsonProperty("integration_id")
        public Integer integrationId;

        @JsonProperty("has_parent_transaction")
        public Boolean hasParentTransaction;

        public PaymobOrder order;

        @JsonProperty("created_at")
        public String createdAt;

        public String currency;

        @JsonProperty("source_data")
        public PaymobSourceData sourceData;

        @JsonProperty("error_occured")
        public Boolean errorOccurred;

        public Integer owner;

        public PaymobData data;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymobOrder {
        public Integer id;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymobSourceData {
        public String type;
        public String pan;

        @JsonProperty("sub_type")
        public String subType;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymobData {
        public String message;

        @JsonProperty("txn_response_code")
        public String txnResponseCode;
    }
}
