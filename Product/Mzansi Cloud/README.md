# Mzansi Cloud

The secure cloud platform behind the Mzansi Connect ecosystem. Stores customer information and device analytics with real-time dashboards and historical reporting for staff and administrators.

> Status: prototype. Review security, data protection, and access-control requirements before any production or commercial deployment.

## Components

| Area | Purpose | Location |
| --- | --- | --- |
| API routes | Unified Skyline IT server mounts the cloud API at `/api/mzansi/cloud/*` | `server/routes/mzansiCloud.js` |
| Data store | Local JSON files (customers, staff, devices, summaries, alerts, audit log) | `data/mzansi/cloud/` |
| Database schema | Original PostgreSQL schema kept for reference | `database/schema.sql` |
| Web dashboard | Admin analytics dashboard with charts, tables, and reporting | `web/` |
| Compliance | South African compliance notes (POPIA, ECTA) | `compliance/` |

## Architecture

```text
Mzansi devices ----+
                   v
Unified Skyline IT server (port 3000)
        |
        +--> /api/mzansi/cloud/*  JSON-backed analytics API
        +--> /cloud/              Web dashboard (static)
```

## Prerequisites

- Node.js 18 or later and npm
- The unified Skyline IT server (`server/index.js`) with its dependencies installed

## Quick start

### 1. Start the unified server

```bash
npm install
node server/index.js
```

The dashboard is served at `http://localhost:3000/cloud/` and the API at `http://localhost:3000/api/mzansi/cloud/`. Seed data is created automatically in `data/mzansi/cloud/` by `server/scripts/seedProducts.js`. Admin endpoints (`/api/mzansi/cloud/customers`, `/api/mzansi/cloud/audit`) require the `X-Api-Key` header with `ADMIN_API_KEY` from the root `.env`.

### 2. Build the Android APK

```bash
cd android
gradle assembleDebug
```

The debug APK is written to `android/app/build/outputs/apk/debug/app-debug.apk`. Copy it to `web/downloads/mzansi-cloud.apk` - the dashboard serves it at `/cloud/downloads/mzansi-cloud.apk` and the Download button points there. Query `/api/mzansi/cloud/app` for the served version and file size.

## API summary

All endpoints below are prefixed with `/api/mzansi/cloud` on the unified server.

| Method | Path | Authentication | Purpose |
| --- | --- | --- | --- |
| `GET` | `/analytics/overview` | None | Platform overview stats |
| `GET` | `/analytics/devices-by-status` | None | Device status counts |
| `GET` | `/analytics/devices-by-type` | None | Device type counts |
| `GET` | `/analytics/summaries?metric=&days=` | None | Daily summary series |
| `GET` | `/analytics/aggregate?metric=` | None | Totals and averages |
| `GET` | `/customers` | `X-API-Key` | Customer list (POPIA: admin only) |
| `GET` | `/devices` | None | Device list with customer names |
| `GET` | `/alerts` | None | Alert feed |
| `GET` | `/audit` | `X-API-Key` | Audit log (admin only) |
| `GET` | `/app` | None | App download info (version, APK URL, size) |
| `GET` | `/health` | None | Server health check |

## Configuration and secrets

The unified server's root `.env` file contains environment-specific values (`ADMIN_API_KEY`, `JWT_SECRET`, etc.) and must not be committed. The root `.gitignore` excludes it. Rotate any credential that has been shared or committed accidentally.

## Security and deployment notes

- Use HTTPS and a unique, high-entropy `ADMIN_API_KEY` for any non-local deployment.
- Customer endpoints are protected by the admin API key; review before exposing the server to the internet.
- Aggregate and pseudonymise data before building public analytics.
- Confirm local requirements for data protection (POPIA) and electronic transactions (ECTA) before field deployment.

## Documentation

- [POPIA notice](compliance/POPIA_NOTICE.md)
- [ECTA notice](compliance/ECTA_NOTICE.md)

## License

The project is licensed under the Apache License 2.0. See [LICENSE](LICENSE).
