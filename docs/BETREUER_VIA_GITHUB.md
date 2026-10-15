# Betreuer via GitHub-ID einrichten

Damit ein echter GitHub-Nutzer die Betreuer-Seite nutzen kann, muss sein Profil in der DB mit seiner numerischen GitHub-ID verknüpft sein.

## 1. GitHub-ID ermitteln

1. Mit GitHub einloggen
2. Auf `/login-debug` gehen
3. **GitHub-ID (numerisch)** notieren (z.B. `12345678`)

## 2. Betreuerprofil anlegen (SQL)

**Option A:** Neues Profil mit deiner GitHub-ID anlegen:

```sql
-- Ersetze 12345678 durch deine echte GitHub-ID aus /login-debug
-- profile_id muss eindeutig sein (z.B. 2000, 2001, … – nicht 1001–1005, die sind Testdaten)
INSERT INTO betreuer_profil (profile_id, github_id, titel, name, email, phone, raum)
VALUES (2000, 12345678, 'NONE', 'Dein Name', 'deine.email@example.de', NULL, '25.12.00.01');

-- Optional: Fachgebiet hinzufügen
INSERT INTO betreuer_fachgebiet (profile_id, fachgebiet) VALUES (2000, 'Software Engineering');
```

**Option B:** Bestehendes Test-Profil auf deine GitHub-ID umbiegen (nur für lokale Entwicklung):

```sql
-- Vorsicht: Betreuer 1001 (Prof. Dr. Anna Schmidt) wird zu deinem Profil
UPDATE betreuer_profil SET github_id = 12345678 WHERE profile_id = 1001;
```

## 3. Prüfen

- Mit GitHub einloggen
- `/betreuerProfil` aufrufen
- Das eigene Betreuerprofil sollte angezeigt werden

---

**Hinweis:** In Produktion sollte ein Admin-Flow „Betreuer hinzufügen“ genutzt werden, statt manueller SQL-Befehle.
