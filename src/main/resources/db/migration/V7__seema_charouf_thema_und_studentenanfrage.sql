-- Prof. Dr. Seema Charouf: Profil-Name setzen, ein Thema anlegen, eine Student-Anfrage (Max Mustermann) dazu

-- 1. Betreuer-Profil 148059646 als Prof. Dr. Seema Charouf
UPDATE betreuer_profil
SET name = 'Seema Charouf', titel = 'PROF_DR'
WHERE profile_id = 148059646;

-- 2. Student 2001 (Max Mustermann) anlegen, falls noch nicht vorhanden (für Anfrage)
INSERT INTO student (profile_id, display_name, created_at)
VALUES (2001, 'Max Mustermann', NOW())
ON CONFLICT (profile_id) DO NOTHING;

-- 3. Ein Thema von Prof. Dr. Seema Charouf (owner 148059646)
INSERT INTO theme (theme_id, owner_profile_id, titel, beschreibung)
VALUES (
    'a0000001-0000-4000-8000-000000000001'::uuid,
    148059646,
    'Abschlussarbeit bei Prof. Dr. Seema Charouf',
    'Thema für Abschlussarbeiten im Bereich Software Engineering.'
)
ON CONFLICT (theme_id) DO NOTHING;

-- 4. Student 2001 fragt dieses Thema an (Anfrage erscheint bei Seema unter "Studentenanfragen")
INSERT INTO student_theme_request (profile_id, theme_id, status, requested_at)
VALUES (2001, 'a0000001-0000-4000-8000-000000000001'::uuid, 'PENDING', NOW())
ON CONFLICT (profile_id, theme_id) DO NOTHING;
