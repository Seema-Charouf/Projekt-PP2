-- Themen werden oft pro Betreuer geladen (Startseite, Anfragen, Admin-Übersicht).
-- PostgreSQL legt für Fremdschlüssel KEINEN Index automatisch an – ohne Index liest die DB die ganze Tabelle.
CREATE INDEX IF NOT EXISTS idx_theme_owner_profile_id ON theme (owner_profile_id);
