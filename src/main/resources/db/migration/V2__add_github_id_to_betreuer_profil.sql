-- GitHub-ID ↔ Betreuerprofil: eindeutige Zuordnung
ALTER TABLE betreuer_profil ADD COLUMN github_id BIGINT;
UPDATE betreuer_profil SET github_id = profile_id WHERE github_id IS NULL;
ALTER TABLE betreuer_profil ALTER COLUMN github_id SET NOT NULL;
ALTER TABLE betreuer_profil ADD CONSTRAINT uk_betreuer_profil_github_id UNIQUE (github_id);
