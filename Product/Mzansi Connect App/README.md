# Mzansi Connect App

The companion mobile application for the Mzansi Connect ecosystem. View device data, receive real-time alerts, track energy usage, monitor security systems, and manage subscriptions - all from your phone.

> Status: prototype. Review security, hardware safety, data protection, and payment-processing requirements before any production or commercial deployment.

## Components

| Area | Purpose | Location |
| --- | --- | --- |
| API routes | Unified Skyline IT server mounts the app API at `/api/mzansi/app/*` | `server/routes/mzansiApp.js` |
| Data store | Local JSON files (devices, readings, alerts, energy, security, plans, subscriptions) | `data/mzansi/app/` |
| Database schema | Original PostgreSQL schema kept for reference | `database/schema.sql` |
| Web app | Mobile-style browser dashboard (devices, alerts, energy, security, subscriptions) | `web/` |
| Android app | Kotlin Android client | `android/` |
| Compliance | South African compliance notes (POPIA, ECTA) | `compliance/` |

## Architecture

```text
Mzansi Connect devices (trackers, security, water, energy, fleet)
        |
        | POST /api/mzansi/app/telemetry (X-API-Key = ADMIN_API_KEY)
        v
Unified Skyline IT server (port 3000)
        |
        +--> /api/mzansi/app/*  JSON-backed API
        +--> /app/              Web dashboard (static)
        +--> Android application (optional)
```

## Prerequisites

- Node.js 18 or later and npm
- The unified Skyline IT server (`server/index.js`) with its dependencies installed
- Android Studio and JDK 17 for the Android app

## Quick start

### 1. Start the unified server

```bash
npm install
node server/index.js
```

The dashboard is served at `http://localhost:3000/app/` and the API at `http://localhost:3000/api/mzansi/app/`. Seed data is created automatically in `data/mzansi/app/` by `server/scripts/seedProducts.js` (run it manually to reseed). Write endpoints (`/api/mzansi/app/telemetry`) require the `X-Api-Key` header with `ADMIN_API_KEY` from the root `.env`.

### 2. Configure the Android application

```bash
cd android
cp local.properties.example local.properties
```

Configure the project for your own backend, then open the `android/` directory in Android Studio and build or run the application.

### 3. Build the Android APK

```bash
cd android
gradle assembleDebug
```

The debug APK is written to `android/app/build/outputs/apk/debug/app-debug.apk`. Copy it to `web/downloads/mzansi-connect-app.apk` - the dashboard serves it at `/app/downloads/mzansi-connect-app.apk` and the Download button points there. Query `/api/mzansi/app/app` for the served version and file size.

## API summary

All endpoints below are prefixed with `/api/mzansi/app` on the unified server.

| Method | Path | Authentication | Purpose |
| --- | --- | --- | --- |
| `POST` | `/telemetry` | `X-API-Key` | Store device telemetry readings |
| `GET` | `/devices` | None | List devices |
| `GET` | `/alerts` | None | Alerts (unacknowledged first) |
| `PUT` | `/alerts/:id/ack` | None | Acknowledge an alert |
| `GET` | `/energy/:deviceId` | None | Energy usage history |
| `GET` | `/security/:deviceId` | None | Security events |
| `GET` | `/plans` | None | Subscription plans |
| `GET` | `/subscriptions` | None | Customer subscriptions |
| `PUT` | `/subscriptions/:id/cancel` | None | Cancel at period end |
| `GET` | `/app` | None | App download info (version, APK URL, size) |

Example telemetry payload:

```json
{
  "serial": "MZ-EM-0001",
  "readings": [
    { "metric": "power_w", "value": 1200.5, "unit": "W" },
    { "metric": "voltage_v", "value": 230.2, "unit": "V" }
  ]
}
```

## Configuration and secrets

The unified server's root `.env` file contains environment-specific values (`ADMIN_API_KEY`, `JWT_SECRET`, etc.) and must not be committed. The root `.gitignore` excludes it. Rotate any credential that has been shared or committed accidentally.

## Security and deployment notes

- Use HTTPS and a unique, high-entropy `ADMIN_API_KEY` for any non-local deployment.
- The public read endpoints must be reviewed before exposing the server to the internet.
- Payment processing must use a PCI-compliant provider; never store card details.
- Confirm local requirements for SIM provisioning, radio equipment, enclosure safety, and personal-information handling before field deployment.

## Documentation

- [POPIA notice](compliance/POPIA_NOTICE.md)
- [ECTA notice](compliance/ECTA_NOTICE.md)

## License

The project is licensed under the Apache License 2.0. See [LICENSE](LICENSE).
