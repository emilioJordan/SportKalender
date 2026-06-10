# SportKalender Backend

Spring-Boot-Backend fuer Events und Tasks mit Keycloak/JWT-Absicherung.

## Wichtige URLs

| Dienst | URL |
| --- | --- |
| Keycloak | http://localhost:8080 |
| Realm Issuer | http://localhost:8080/realms/Sportkalender |
| Backend | http://localhost:9090 |
| Swagger UI | http://localhost:9090/ oder http://localhost:9090/swagger-ui.html |
| OpenAPI JSON | http://localhost:9090/v3/api-docs |
| Frontend | http://localhost:4200 |

## Backend starten

```powershell
cd D:\Users\jorm\Downloads\SportKalender-main\SportKalender-main
.\mvnw.cmd spring-boot:run
```

Lokal wird standardmaessig das Profil `local` verwendet. Dadurch startet das Backend mit einer H2-In-Memory-Datenbank und benoetigt keinen lokalen PostgreSQL-Server.

## Keycloak starten

Starte deine lokal installierte Keycloak-Instanz auf Port `8080`.

Typischer Start aus dem Keycloak-Installationsordner:

```powershell
cd <dein-keycloak-ordner>\bin
.\kc.bat start-dev --http-port 8080
```

Falls Keycloak beim ersten Start noch keinen Admin-Benutzer hat:

```powershell
$env:KEYCLOAK_ADMIN='admin'
$env:KEYCLOAK_ADMIN_PASSWORD='admin'
.\kc.bat start-dev --http-port 8080
```

Danach ist die Admin-Konsole unter http://localhost:8080 erreichbar.

Die reproduzierbare Realm-Konfiguration liegt in:

```txt
keycloak/Sportkalender-realm.json
```

Eine vollstaendige Schritt-fuer-Schritt-Anleitung steht in [docs/keycloak-setup.md](docs/keycloak-setup.md).

## Keycloak-Konfiguration

| Einstellung | Wert |
| --- | --- |
| Realm | Sportkalender |
| Client | sportkalender-frontend |
| Client type | OpenID Connect |
| Client authentication | Off/Public client |
| Standard flow | Enabled |
| Valid redirect URIs | http://localhost:4200/* |
| Web origins | http://localhost:4200 |

Das Backend akzeptiert JWTs vom Issuer:

```txt
http://localhost:8080/realms/Sportkalender
```

Frontend-Requests an geschuetzte Endpunkte muessen diesen Header senden:

```http
Authorization: Bearer <access_token>
```

## Rollen und Demo-User

| Benutzer | Passwort | Rolle | Zugriff |
| --- | --- | --- | --- |
| user | user | ROLE_USER | Events/Tasks lesen |
| admin | admin | ROLE_ADMIN | Events/Tasks lesen, erstellen, bearbeiten, loeschen |

Das Backend akzeptiert ausserdem `READ` als Lese-Rolle und `UPDATE` als Schreib-Rolle.

## Geschuetzte Endpunkte

| Endpoint | Ohne Token | ROLE_USER | ROLE_ADMIN |
| --- | --- | --- | --- |
| GET /api/event | 401 | erlaubt | erlaubt |
| GET /api/task | 401 | erlaubt | erlaubt |
| GET /api/auth/claims | 401 | erlaubt | erlaubt |
| POST /api/event, /api/task | 401 | 403 | erlaubt |
| PUT /api/event, /api/task | 401 | 403 | erlaubt |
| DELETE /api/event, /api/task | 401 | 403 | erlaubt |

## Testen mit Swagger

1. Keycloak starten und Realm importieren.
2. Backend starten.
3. Swagger UI oeffnen: http://localhost:9090/
4. Access Token fuer `user` oder `admin` holen, siehe [docs/keycloak-setup.md](docs/keycloak-setup.md).
5. In Swagger `Authorize` anklicken.
6. Access Token einfuegen.
7. GET-Endpunkte mit `user`, Schreib-Endpunkte mit `admin` testen.
