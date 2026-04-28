-- Migration: Update Roles enum values from ROLE_* to *
-- This script updates the roles column to match the new enum values

UPDATE users SET role = 'ADMIN' WHERE role = 'ROLE_ADMIN';
UPDATE users SET role = 'ATTENDEE' WHERE role = 'ROLE_ATTENDEE';
UPDATE users SET role = 'ORGANIZER' WHERE role = 'ROLE_ORGANIZER';

-- Verify the update was successful
SELECT DISTINCT role FROM users;
