# PayMob Payment Integration - UserId & EventId Mapping Solution

## Problem
When PayMob sends a callback webhook after payment, we need to know which user (`userId`) and which event (`eventId`) the payment is for. However, PayMob only provides its own identifiers (`obj.owner` and `obj.order.id`), which don't directly correspond to our local user and event IDs.

## Solution
We store a **temporary payment mapping** before redirecting the user to PayMob, then retrieve it in the callback to complete the registration.

---

## How It Works

### Step 1: Payment Initiation (`payWithCard`)

When a user initiates a payment:

```java
public String payWithCard(int amountCents, int userId, int eventId) {
    // ... authenticate and create PayMob order ...
    int orderId = createOrder(token, amountCents);
    
    // ✅ STORE THE MAPPING
    Payment pendingPayment = new Payment();
    pendingPayment.setAmount(BigDecimal.valueOf(amountCents).divide(BigDecimal.valueOf(100)));
    pendingPayment.setPaymentStatus(PaymentStatus.PENDING);
    pendingPayment.setTransactionDate(LocalDateTime.now());
    pendingPayment.setOrderId(orderId);        // ← PayMob order ID
    pendingPayment.setUserId(userId);          // ← Local user ID  
    pendingPayment.setEventId(eventId);        // ← Local event ID
    paymentRepository.save(pendingPayment);
    
    return iframeUrl;
}
```

**What happens:**
- A `Payment` record is created and saved to the database
- Contains: `orderId` (from PayMob), `userId`, `eventId`
- Status: `PENDING` (waiting for payment confirmation)
- This record acts as a **lookup table** for the webhook

### Step 2: Payment Processing (PayMob Callback)

When PayMob sends the callback webhook:

```java
public boolean paymobCallback(PaymobCallbackPayload payload, String hmacHeader) {
    // ... verify HMAC signature ...
    
    PaymobObj obj = payload.obj;
    
    // ✅ RETRIEVE THE STORED MAPPING
    Payment payment = paymentRepository.findByOrderId(obj.order.id)
        .orElseThrow(() -> new RuntimeException("Payment record not found"));
    
    int userId = payment.getUserId();      // ← Retrieved from storage
    int eventId = payment.getEventId();    // ← Retrieved from storage
    
    log.info("Retrieved userId: {}, eventId: {} from payment mapping", userId, eventId);
    
    // ✅ UPDATE PAYMENT RECORD
    payment.setPaymentStatus(obj.success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
    paymentRepository.save(payment);
    
    // ✅ REGISTER USER TO EVENT (only if payment successful)
    if (Boolean.TRUE.equals(obj.success)) {
        registrationService.registerUserToEvent(new RegistrationRequestDto() {{
            setUserId(userId);      // ← Using retrieved userId
            setEventId(eventId);    // ← Using retrieved eventId
        }});
    }
    
    return Boolean.TRUE.equals(obj.success);
}
```

**What happens:**
- PayMob webhook is received with `obj.order.id`
- We find the `Payment` record using `findByOrderId()`
- Extract stored `userId` and `eventId`
- Update payment status based on PayMob response
- Register user to event if successful

---

## Database Schema

The `payments` table now includes:

| Column | Type | Purpose |
|--------|------|---------|
| `id` | INT | Primary key |
| `order_id` | INT | **PayMob order ID** (used to find the record) |
| `user_id` | INT | **Local user ID** (who is paying) |
| `event_id` | INT | **Local event ID** (what they're paying for) |
| `amount` | DECIMAL | Payment amount |
| `payment_status` | VARCHAR | PENDING, SUCCESS, FAILED |
| `transaction_date` | TIMESTAMP | When the payment was processed |

---

## Data Flow Diagram

```
┌──────────────────┐
│   User clicks    │
│  "Pay with Card" │
└────────┬─────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│ payWithCard(100, userId=5, eventId=12)   │
│                                          │
│ 1. Create PayMob order → orderId=999     │
│ 2. Get payment key                       │
│ 3. Store Payment record:                 │
│    - orderId: 999                        │
│    - userId: 5                           │
│    - eventId: 12                         │
│    - status: PENDING                     │
│ 4. Return PayMob iframe URL              │
└────────┬─────────────────────────────────┘
         │
         ▼
┌──────────────────────┐
│  User fills payment  │
│   form in PayMob     │
│  iframe and confirms │
└────────┬─────────────┘
         │
    (User's bank processes payment)
         │
         ▼
┌──────────────────────────────────────────────┐
│  PayMob Webhook: POST /api/payments/callback │
│  {                                           │
│    "obj": {                                  │
│      "id": 12345,        (transaction ID)    │
│      "order": {                              │
│        "id": 999         ← matches orderId   │
│      },                                      │
│      "success": true,                        │
│      ...                                     │
│    }                                         │
│  }                                           │
└────────┬─────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│ paymobCallback(payload)                  │
│                                          │
│ 1. Verify HMAC signature ✓               │
│ 2. Find Payment by orderId=999           │
│    → Gets record with userId=5, eventId=12
│ 3. Update payment status: SUCCESS        │
│ 4. Call registerUserToEvent(5, 12)       │
│    → User 5 is now registered to event 12│
│ 5. Return 200 to PayMob                  │
└──────────────────────────────────────────┘
         │
         ▼
   Registration Complete!
```

---

## Key Modifications

### 1. Payment Entity (`Payment.java`)
```java
@Column
private int orderId;    // Track PayMob order

@Column
private int userId;     // Store for callback

@Column
private int eventId;    // Store for callback

// Added getters & setters for above fields
```

### 2. Payment Repository (`PaymentRepo.java`)
```java
Optional<Payment> findByOrderId(int orderId);
```

### 3. PayMob Service (`PayMobService.java`)
- **`payWithCard()`**: Now stores Payment record before redirecting to PayMob
- **`paymobCallback()`**: Now retrieves userId/eventId from Payment record instead of using PayMob data

---

## Benefits

✅ **Reliable Mapping**: No dependency on PayMob's user identifiers  
✅ **Audit Trail**: Payment records store complete transaction history  
✅ **Idempotent**: Webhooks can be retried without duplicate registrations  
✅ **Flexible**: Can extend with additional payment metadata  
✅ **Queryable**: Can find all payments for a user or event  

---

## Example Usage

```bash
# User wants to pay 500 EGP for event 12
GET /api/payments/pay?amountCents=50000&userId=5&eventId=12

# Response: PayMob iframe URL
# User fills payment form...

# PayMob sends callback webhook
POST /api/payments/callback
{
  "obj": {
    "id": 12345,
    "order": {"id": 999},
    "success": true,
    ...
  }
}

# ✅ User 5 is now registered to event 12
```

---

## Future Enhancements

1. **Add payment method metadata** - Track card type, masked card number
2. **Add email notifications** - Send confirmation after successful payment
3. **Add retry logic** - Automatic retry for failed payments
4. **Add reconciliation** - Daily sync with PayMob to verify all payments
5. **Add cancellation support** - Allow users to cancel pending payments
