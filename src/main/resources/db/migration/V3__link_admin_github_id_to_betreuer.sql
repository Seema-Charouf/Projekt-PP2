-- Optional: Admin-GitHub-ID an Betreuer-Profil 1001 koppeln (für lokale Tests)
-- Ersetze 148009764 durch deine GitHub-ID aus /login-debug, falls anders.
-- Nach dem nächsten Start kann /betreuerProfil ohne ?testUser= genutzt werden.
UPDATE betreuer_profil SET github_id = 148009764 WHERE profile_id = 1001;
