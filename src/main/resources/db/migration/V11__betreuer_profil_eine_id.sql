-- Vorher gab es zwei IDs (profile_id und github_id). Der Code hat beim Speichern aber immer
-- github_id = profile_id gesetzt – eine abweichende Zuordnung ging also beim nächsten Speichern verloren.
-- Jetzt gilt überall: profile_id ist die numerische GitHub-ID (wie bei Studierenden und ADMIN_IDS).
ALTER TABLE betreuer_profil DROP CONSTRAINT IF EXISTS uk_betreuer_profil_github_id;
ALTER TABLE betreuer_profil DROP COLUMN github_id;
