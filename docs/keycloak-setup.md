# Keycloak Setup fuer SportKalender

Diese Anleitung richtet die lokale Keycloak-Konfiguration fuer das SportKalender-Backend und das Angular-Frontend reproduzierbar ein.

## Zielkonfiguration

| Komponente | Wert |
| --- | --- |
| Keycloak | http://localhost:8080 |
| Realm | Sportkalender |
| Realm Issuer | http://localhost:8080/realms/Sportkalender |
| Backend | http://localhost:9090 |
| Swagger UI | http://localhost:9090/ oder http://localhost:9090/swagger-ui.html |
| OpenAPI JSON | http://localhost:9090/v3/api-docs |
| Frontend | http://localhost:4200 |
| Frontend Client ID | sportkalender-frontend |

Das Backend erwartet Bearer Tokens in diesem Format:

```http
Authorization: Bearer <access_token>
```

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

## Realm importieren

Die Importdatei liegt hier:

```txt
keycloak/Sportkalender-realm.json
```

Import ueber die Keycloak Admin-Konsole:

1. http://localhost:8080 oeffnen.
2. Mit deinem Keycloak-Admin-Benutzer anmelden.
3. Realm-Auswahl oeffnen.
4. `Create realm` auswaehlen.
5. Datei `keycloak/Sportkalender-realm.json` importieren.
6. Realm `Sportkalender` erstellen.

Wenn du schon einen Demo-Realm hast, kannst du ihn als Vorlage nehmen. Wichtig ist nur, dass der neue Realm exakt `Sportkalender` heisst und der Issuer dadurch `http://localhost:8080/realms/Sportkalender` wird.

## Client-Konfiguration

Der importierte Client heisst `sportkalender-frontend`.

Wichtige Einstellungen:

| Einstellung | Wert |
| --- | --- |
| Client type | OpenID Connect |
| Client authentication | Off |
| Access type | Public client |
| Standard flow | Enabled |
| Direct access grants | Enabled fuer lokale Token-Tests |
| Valid redirect URIs | http://localhost:4200/* |
| Web origins | http://localhost:4200 |
| PKCE | S256 |

## Rollen

Das Backend bevorzugt diese Rollen:

| Rolle | Zugriff |
| --- | --- |
| ROLE_USER | Events und Tasks lesen |
| ROLE_ADMIN | Events und Tasks lesen, erstellen, bearbeiten und loeschen |

Aus Kompatibilitaetsgruenden akzeptiert das Backend auch diese Rollen:

| Rolle | Zugriff |
| --- | --- |
| READ | Lesen, wird intern wie ROLE_USER behandelt |
| UPDATE | Schreiben, wird intern wie ROLE_ADMIN behandelt |

Die Rollen koennen als Realm-Rollen oder als Client-Rollen im Client `sportkalender-frontend` im JWT stehen. Das Backend liest beide Varianten.

## Demo-Benutzer

Die Importdatei enthaelt lokale Demo-Benutzer. Diese Passwoerter sind nur fuer die lokale Abgabe/Demo gedacht.

| Benutzer | Passwort | Rolle |
| --- | --- | --- |
| user | user | ROLE_USER |
| admin | admin | ROLE_ADMIN |

## Backend-Konfiguration

Die relevante Spring-Konfiguration steht in `src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/Sportkalender

app:
  security:
    client-id: sportkalender-frontend
```

CORS erlaubt das Angular-Frontend:

```txt
Origin: http://localhost:4200
Methods: GET, POST, PUT, DELETE, OPTIONS
Headers: Authorization, Content-Type
```

## Backend starten

```powershell
cd D:\Users\jorm\Downloads\SportKalender-main\SportKalender-main
.\mvnw.cmd spring-boot:run
```

Das Backend verwendet lokal standardmaessig das Profil `local` und startet mit H2 auf Port `9090`.

## Token fuer Tests holen

Mit dem Demo-Admin:

```powershell
$body = @{
  client_id = 'sportkalender-frontend'
  grant_type = 'password'
  username = 'admin'
  password = 'admin'
  scope = 'openid profile'
}
$response = Invoke-RestMethod -Method Post `
  -Uri 'http://localhost:8080/realms/Sportkalender/protocol/openid-connect/token' `
  -Body $body
$response.access_token
```

Mit dem normalen Demo-User entsprechend `username = 'user'` und `password = 'user'` verwenden.

## Swagger testen

1. Backend starten.
2. Swagger UI oeffnen: http://localhost:9090/
3. `Authorize` anklicken.
4. In das Feld nur den Access Token einfuegen, ohne `Bearer` davor.
5. GET `/api/event` oder GET `/api/task` mit `user` oder `admin` testen.
6. POST/PUT/DELETE mit `admin` testen.

## Erwartetes Verhalten

| Test | Erwartung |
| --- | --- |
| GET http://localhost:9090/v3/api-docs ohne Token | 200 |
| GET http://localhost:9090/api/event ohne Token | 401 |
| GET http://localhost:9090/api/task ohne Token | 401 |
| GET /api/event mit ROLE_USER | 200 |
| GET /api/task mit ROLE_USER | 200 |
| POST/PUT/DELETE mit ROLE_USER | 403 |
| POST/PUT/DELETE mit ROLE_ADMIN | erlaubt |
| Frontend unter http://localhost:4200 | leitet zu Keycloak und zurueck |
| Frontend API Requests | senden `Authorization: Bearer <access_token>` |
