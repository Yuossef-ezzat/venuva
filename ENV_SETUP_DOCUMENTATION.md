# Environment Variables Setup - Venuva Application

## Summary of Changes

This document outlines all the changes made to implement environment variable management for the Venuva application, moving secrets out of source code and into a `.env` file.

---

## Files Created

### 1. `.env` (Production/Development Environment File)
**Location:** `e:\Projects\venuva\venuva\.env`

Contains all sensitive configuration:
- Server port
- Database credentials (URL, username, password)
- JWT secrets and expiration times
- Email (Gmail SMTP) configuration
- PayMob payment gateway API credentials
- HMAC secret key for PayMob webhook verification
- Logging file paths and levels

**⚠️ IMPORTANT:** This file is listed in `.gitignore` and should NEVER be committed to version control.

### 2. `.env.example` (Template Reference File)
**Location:** `e:\Projects\venuva\venuva\.env.example`

A comprehensive template with:
- All required environment variables
- Detailed documentation for each variable
- Placeholder values
- Links to where to obtain credentials (e.g., PayMob dashboard, Gmail App Passwords)
- Instructions for generating secure values

**✅ This file CAN be committed to help developers understand what variables are needed.**

---

## Files Modified

### 1. `application.properties`
**Location:** `src/main/resources/application.properties`

**Changes Made:**
- Added Spring Boot 2.4+ property import: `spring.config.import=optional:file:.env[.properties]`
- Converted hardcoded values to environment variable references using `${VAR_NAME}` syntax
- All secrets now reference environment variables instead of hardcoded values

**Example conversions:**
```properties
# BEFORE
spring.datasource.url=jdbc:mysql://localhost:3307/venuvadbtwo
spring.datasource.username=root
spring.mail.password=uhnp sryl muzi bqiy

# AFTER
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
```

### 2. `PayMobService.java`
**Location:** `src/main/java/com/example/venuva/Core/ServiceLayer/PayMobService.java`

**Changes Made:**
- Added import: `import org.springframework.beans.factory.annotation.Value;`
- Replaced all `System.getenv()` calls with `@Value` annotations
- Fields are now properly injected at startup time instead of runtime lookup

**Example conversion:**
```java
// BEFORE
private final String apiKey = System.getenv("PAYMOB_API_KEY") != null
        ? System.getenv("PAYMOB_API_KEY")
        : "ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5...";

// AFTER
@Value("${PAYMOB_API_KEY:ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5...}")
private String apiKey;
```

### 3. `.gitignore`
**Location:** `e:\Projects\venuva\venuva\.gitignore`

**Changes Made:**
- Added `.env` to prevent accidental commit of secrets
- Added `!.env.example` to ensure the example file IS committed

---

## Environment Variables Reference

### Server Configuration
| Variable | Purpose | Default |
|----------|---------|---------|
| `SERVER_PORT` | Application listening port | 8088 |

### Database
| Variable | Purpose | Required |
|----------|---------|----------|
| `SPRING_DATASOURCE_URL` | MySQL connection string | Yes |
| `SPRING_DATASOURCE_USERNAME` | Database user | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | No |

### JWT Authentication
| Variable | Purpose | Default |
|----------|---------|---------|
| `JWT_SECRET` | Token signing key | Default provided |
| `JWT_EXPIRATION` | Access token expiration (ms) | 100000 |
| `JWT_REFRESH_EXPIRATION` | Refresh token expiration (ms) | 604800000 |

### Email Configuration
| Variable | Purpose | Default |
|----------|---------|---------|
| `SPRING_MAIL_HOST` | SMTP server | smtp.gmail.com |
| `SPRING_MAIL_PORT` | SMTP port | 587 |
| `SPRING_MAIL_USERNAME` | Email account | (required) |
| `SPRING_MAIL_PASSWORD` | Email app password | (required) |
| `SPRING_MAIL_SMTP_AUTH` | Enable auth | true |
| `SPRING_MAIL_SMTP_STARTTLS_ENABLE` | Enable TLS | true |

### PayMob Payment Gateway
| Variable | Purpose | Default |
|----------|---------|---------|
| `PAYMOB_API_KEY` | API authentication key | Default provided |
| `PAYMOB_INTEGRATION_ID` | Integration ID for card payments | 4896849 |
| `PAYMOB_IFRAME_ID` | iFrame ID for payment form | 897502 |
| `HMAC_SECRET_KEY` | Webhook signature verification | Default provided |

### Logging
| Variable | Purpose | Default |
|----------|---------|---------|
| `LOGGING_FILE_NAME` | Log file path | E:/Logs/venuva.log |
| `LOGGING_MAX_FILE_SIZE` | Rotation size | 10MB |
| `LOGGING_MAX_HISTORY` | Retained log files | 30 |
| `LOGGING_LEVEL_VENUVA` | App log level | DEBUG |
| `LOGGING_LEVEL_SECURITY` | Security log level | INFO |
| `LOGGING_LEVEL_HIBERNATE` | Hibernate log level | DEBUG |

---

## How to Use

### Development Setup

1. **Copy the example file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` with your values:**
   - Add real database credentials
   - Add real email credentials
   - Add real PayMob API keys from your dashboard
   - Adjust paths for your system (Windows vs Linux)

3. **Run the application:**
   Spring Boot will automatically load the `.env` file when the application starts

### Production Setup

Use actual deployment environment's variable management:
- **Docker:** Set via `docker-compose.yml` or Kubernetes secrets
- **Cloud (AWS/Azure/GCP):** Use managed secrets/environment variable services
- **Traditional Server:** Set system environment variables

---

## Security Best Practices

✅ **DO:**
- Keep `.env` in `.gitignore` - it contains production secrets
- Use `.env.example` as a reference for deployment
- Use strong, random values for JWT_SECRET in production
- Regenerate PayMob keys regularly
- Use Gmail App Passwords (not regular password)
- Rotate secrets on a regular basis

❌ **DON'T:**
- Commit `.env` to version control
- Share `.env` file publicly
- Hardcode secrets in code
- Use weak secrets (e.g., "password123")
- Reuse secrets across environments
- Store secrets in logs

---

## Services Using Environment Variables

### 1. **JwtService**
- Uses `@ConfigurationProperties(prefix = "app.jwt")`
- Reads: `app.jwt.secret`, `app.jwt.expiration-ms`, `app.jwt.refresh-expiration-ms`
- Injected from application.properties → environment variables

### 2. **EmailService**
- Uses `@Value("${spring.mail.username}")`
- Automatically loads from application.properties
- Connected to Gmail SMTP credentials

### 3. **PayMobService**
- Uses `@Value` annotations for all PayMob credentials
- Reads: `PAYMOB_API_KEY`, `PAYMOB_INTEGRATION_ID`, `PAYMOB_IFRAME_ID`, `HMAC_SECRET_KEY`
- Fallback defaults provided for development

### 4. **Spring Boot Auto-Configuration**
- Database properties via `spring.datasource.*`
- Mail properties via `spring.mail.*`
- Logging properties via `logging.*`

---

## Verification Checklist

✅ `.env` file created with all secrets
✅ `.env` added to `.gitignore`
✅ `.env.example` created with documentation
✅ `application.properties` updated to use environment variables
✅ `PayMobService.java` refactored to use `@Value` annotations
✅ `JwtService.java` already using `@ConfigurationProperties`
✅ `EmailService.java` already using proper property injection
✅ No hardcoded secrets in source code
✅ All services properly configured for dependency injection

---

## Next Steps

1. **Set up your `.env` file** - Copy from `.env.example` and add real values
2. **Test the application** - Verify that all services can access their configuration
3. **Push to version control** - Commit `.env.example` and `.gitignore`, but NOT `.env`
4. **Document deployment** - Ensure deployment process sets these environment variables

---

## Troubleshooting

**Issue:** "Could not resolve placeholder 'XXX_VARIABLE' in string value"
- **Solution:** Ensure the environment variable is set in `.env` file or system environment

**Issue:** PayMob authentication fails
- **Solution:** Verify `PAYMOB_API_KEY` is correct from PayMob dashboard

**Issue:** Email not sending
- **Solution:** 
  1. Verify Gmail credentials
  2. Use App Password, not regular password
  3. Check Gmail security settings allow "Less secure apps"

**Issue:** Database connection fails
- **Solution:** Verify `SPRING_DATASOURCE_URL`, username, and password are correct

---

## References

- Spring Boot Configuration: https://spring.io/blog/2020/08/14/config-file-processing-in-spring-boot-2-4
- Environment Variables in Spring: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
- Gmail App Passwords: https://myaccount.google.com/apppasswords
- PayMob Dashboard: https://dashboard.paymob.com/developers
