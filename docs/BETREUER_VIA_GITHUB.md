# Betreuer via GitHub-ID einrichten

Damit ein echter GitHub-Nutzer die Betreuer-Seite nutzen kann, muss sein Profil in der DB mit seiner numerischen GitHub-ID verknüpft sein.

## 1. GitHub-ID ermitteln

1. Mit GitHub einloggen
2. Auf `/login-debug` gehen
3. **GitHub-ID (numerisch)** notieren (z.B. `12345678`)

## 2. Betreuerprofil anlegen (SQL)

Die `profile_id` eines Betreuers **ist** seine numerische GitHub-ID (seit Migration V11 gibt es keine
separate Spalte `github_id` mehr – genau wie bei Studierenden und `ADMIN_IDS`).

```sql
-- Ersetze 12345678 durch deine echte GitHub-ID aus /login-debug
INSERT INTO betreuer_profil (profile_id, titel, name, email, phone, raum)
VALUES (12345678, 'NONE', 'Dein Name', 'deine.email@example.de', NULL, '25.12.00.01');

-- Optional: Fachgebiet hinzufügen
INSERT INTO betreuer_fachgebiet (profile_id, fachgebiet) VALUES (12345678, 'Software Engineering');
```

## 3. Prüfen

- Mit GitHub einloggen
- `/betreuerProfil` aufrufen
- Das eigene Betreuerprofil sollte angezeigt werden

---

**Hinweis:** In Produktion sollte ein Admin-Flow „Betreuer hinzufügen“ genutzt werden, statt manueller SQL-Befehle.
