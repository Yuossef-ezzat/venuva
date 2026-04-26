# Admin Controller API Documentation

Base URL: `/api/admin/organizers`

---

## Endpoints

### 1. Get All Organizers
**Endpoint:** `GET /api/admin/organizers`

**Description:** Retrieve a list of all organizers in the system.

**Request:**
- No parameters required

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com"
    },
    {
      "id": 2,
      "name": "Jane Smith",
      "email": "jane@example.com"
    }
  ]
}
```

**Status Codes:**
- `200 OK` - Successfully retrieved organizers
- `404 NOT_FOUND` - No organizers found

---

### 2. Get Organizer by ID
**Endpoint:** `GET /api/admin/organizers/{userId}`

**Description:** Retrieve a specific organizer by their user ID.

**Request:**
- **Path Parameter:**
  - `userId` (integer) - The ID of the organizer

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }
}
```

**Status Codes:**
- `200 OK` - Successfully retrieved organizer
- `404 NOT_FOUND` - Organizer not found

---

### 3. Update Organizer
**Endpoint:** `PUT /api/admin/organizers/{userId}`

**Description:** Update organizer information (name and email).

**Request:**
- **Path Parameter:**
  - `userId` (integer) - The ID of the organizer to update
- **Body:**
```json
{
  "newName": "John Updated",
  "newEmail": "john.updated@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "data": true
}
```

**Status Codes:**
- `200 OK` - Successfully updated organizer
- `400 BAD_REQUEST` - Invalid request

---

### 4. Delete Organizer
**Endpoint:** `DELETE /api/admin/organizers/{userId}`

**Description:** Delete an organizer from the system.

**Request:**
- **Path Parameter:**
  - `userId` (integer) - The ID of the organizer to delete

**Response:**
```json
{
  "success": true,
  "data": true
}
```

**Status Codes:**
- `200 OK` - Successfully deleted organizer
- `404 NOT_FOUND` - Organizer not found

---

## Response Format

All responses follow the `Result<T>` wrapper format:

```json
{
  "success": boolean,
  "data": <response_data> | null,
  "error": <error_message> | null
}
```

---

## Example Usage

### Get All Organizers
```bash
curl -X GET http://localhost:8080/api/admin/organizers
```

### Get Organizer by ID
```bash
curl -X GET http://localhost:8080/api/admin/organizers/1
```

### Update Organizer
```bash
curl -X PUT http://localhost:8080/api/admin/organizers/1 \
  -H "Content-Type: application/json" \
  -d '{"newName": "John Updated", "newEmail": "john@updated.com"}'
```

### Delete Organizer
```bash
curl -X DELETE http://localhost:8080/api/admin/organizers/1
```

---

## DTOs

### OrganizerDto
```json
{
  "id": integer,
  "name": string,
  "email": string
}
```

### UpdatedOrganzier
```json
{
  "newName": string,
  "newEmail": string
}
```
