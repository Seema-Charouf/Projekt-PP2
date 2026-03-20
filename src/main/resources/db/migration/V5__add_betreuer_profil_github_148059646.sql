-- Neues Betreuer-Profil für GitHub-ID 148059646 (Variante A: eigenes Profil anlegen)
-- Nach dem nächsten Login wird dieser Nutzer als Betreuer erkannt und auf /betreuerProfil weitergeleitet.
-- Name, E-Mail und Raum können danach unter "Profil bearbeiten" angepasst werden.
INSERT INTO betreuer_profil (profile_id, github_id, titel, name, email, phone, raum)
VALUES (
    148059646,
    148059646,
    'NONE',
    'Neues Betreuer-Profil',
    '148059646@placeholder.local',
    NULL,
    '—'
)
ON CONFLICT (profile_id) DO NOTHING;
