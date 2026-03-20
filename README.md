= Thesis is coming!

== GitHub OAuth2 Login

Für den Login werden Umgebungsvariablen benötigt:

* `CLIENT_ID` – GitHub OAuth App Client ID
* `CLIENT_SECRET` – GitHub OAuth App Client Secret

Optional (für Admin-Zugriff): `ADMIN_IDS=12345678,87654321` – GitHub-numerische IDs mit ROLE_ADMIN

Optional (nur Dev): `TEST_USER_ENABLED=true` – ermöglicht `?testUser=1001` auf der Betreuer-Seite für lokale Tests

**Betreuer-Zugriff:** Ein Nutzer kann die Betreuer-Seite nur nutzen, wenn ein Betreuerprofil mit seiner GitHub-ID in der DB existiert. Die numerische GitHub-ID steht nach dem Login auf `/login-debug`. Anleitung zum Anlegen: siehe `docs/BETREUER_VIA_GITHUB.md`.

*GitHub OAuth App Callback URL:* `http://localhost:8080/login/oauth2/code/github` (Port anpassen bei anderem Server-Port)

=== Umgebungsvariablen setzen

Kopiere `.env.example` nach `.env` und trage deine Werte ein.

*IntelliJ:*
1. Run → Edit Configurations
2. Wähle deine `Thesis404Application`- oder `bootRun`-Konfiguration
3. Unter *Environment variables* entweder:
   * *EnvFile*: Aktiviere „Enable EnvFile“, füge `.env` hinzu
   * Oder manuell: `CLIENT_ID`, `CLIENT_SECRET` (und optional `ADMIN_IDS`) eintragen

*PowerShell:*
`$env:CLIENT_ID="..."; $env:CLIENT_SECRET="..."; ./gradlew bootRun`

*Troubleshooting:* Wenn in der OAuth-Redirect-URL `client_id=${CLIENT_ID}` oder `client_id=$%7BCLIENT_ID%7D` steht, sind die Umgebungsvariablen nicht gesetzt. Die App startet in diesem Fall nicht (Startup-Check).

---

Anforderungen finden Sie in der https://propra.d.stups.hhu.de/ws25/627b529bc074bf7/index.html[Projektbeschreibung] 
Lesen Sie auf jeden Fall die Hinweise im https://propra.d.stups.hhu.de/ws25/81409548708bb5d/index.html[Organisationsdokument]. 



![404 Notes](404.md)

Fristen für die Selbstbewertung in Ilias:

* erste Klausur: 16.02.2026
* zweite Klausur: 17.03.2026

Jedes Teammitglied muss die Selbstbewertung einzeln ausfüllen. 
Wenn Sie den Fragebogen zum ersten Termin ausgefüllt haben, die erste Klausur aber nicht mitgeschrieben haben, müssen Sie den Fragebogen für den zweiten Versuch neu ausfüllen. 

Dokumentieren Sie die Verwendung von KI-Werkzeugen in der Datei ki.txt

Wir behalten uns vor, die Implementierung und Ihre Beantwortung des Fragebogens stichpunktartig zu prüfen.
