-- Die Studentenanfrage (Max Mustermann → Thema von Prof. Dr. Seema Charouf) aus der DB löschen
DELETE FROM student_theme_request
WHERE profile_id = 2001
  AND theme_id = 'a0000001-0000-4000-8000-000000000001'::uuid;
