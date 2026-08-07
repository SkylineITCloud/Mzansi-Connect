# Mzansi Marketplace

A platform where local technicians install Mzansi Connect devices, provide support, and earn income - creating jobs while expanding technology access across South African communities.

> Status: prototype. Review security, data protection, payment, and independent-contractor requirements before any production or commercial deployment.

## Components

| Area | Purpose | Location |
| --- | --- | --- |
| API routes | Unified Skyline IT server mounts the marketplace API at `/api/mzansi/marketplace/*` | `server/routes/mzansiMarketplace.js` |
| Data store | Local JSON files (customers, technicians, jobs, applications, installs, payouts) | `data/mzansi/marketplace/` |
| Database schema | Original PostgreSQL schema kept for reference | `database/schema.sql` |
| Web app | Jobs board, technician profiles, and earnings dashboard | `web/` |
| Compliance | South African compliance notes (POPIA, ECTA) | `compliance/` |

## Architecture

```text
Customers (post jobs)          Technicians (apply + earn)
        |                              |
        v                              v
Unified Skyline IT server (port 3000)
        |
        +--> /api/mzansi/marketplace/*  JSON-backed API
        +--> /marketplace/              Web dashboard (static)
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

The dashboard is served at `http://localhost:3000/marketplace/` and the API at `http://localhost:3000/api/mzansi/marketplace/`. Seed data is created automatically in `data/mzansi/marketplace/` by `server/scripts/seedProducts.js`. Write endpoints (`/jobs`, `/jobs/:id/apply`, `/jobs/:id/assign`, `/jobs/:id/status`, `/technicians`) require the `X-Api-Key` header with `ADMIN_API_KEY` from the root `.env`.

### 2. Build the Android APK

```bash
cd android
gradle assembleDebug
```

The debug APK is written to `android/app/build/outputs/apk/debug/app-debug.apk`. Copy it to `web/downloads/mzansi-marketplace.apk` - the dashboard serves it at `/marketplace/downloads/mzansi-marketplace.apk` and the Download button points there. Query `/api/mzansi/marketplace/app` for the served version and file size.

## API summary

All endpoints below are prefixed with `/api/mzansi/marketplace` on the unified server.

| Method | Path | Authentication | Purpose |
| --- | --- | --- | --- |
| `GET` | `/jobs` | None | Open jobs list |
| `GET` | `/jobs/:id` | None | Single job detail |
| `POST` | `/jobs` | `X-API-Key` | Post a job |
| `POST` | `/jobs/:id/apply` | `X-API-Key` | Technician applies |
| `PUT` | `/jobs/:id/assign` | `X-API-Key` | Assign a technician |
| `PUT` | `/jobs/:id/status` | `X-API-Key` | Advance job status (creates install + payout on completion) |
| `GET` | `/technicians` | None | Verified technicians |
| `GET` | `/technicians/:id` | None | Technician profile |
| `POST` | `/technicians` | `X-API-Key` | Register a technician |
| `GET` | `/earnings/:technicianId` | None | Technician earnings |
| `GET` | `/app` | None | App download info (version, APK URL, size) |
| `GET` | `/health` | None | Server health check |

Example job payload:

```json
{
  "title": "Install Spaza Shop Security",
  "description": "Mount motion sensor and panic button",
  "device_type": "spaza_security",
  "town": "Umlazi",
  "province": "KwaZulu-Natal",
  "pay_rand": 850
}
```

## Configuration and secrets

The unified server's root `.env` file contains environment-specific values (`ADMIN_API_KEY`, `JWT_SECRET`, etc.) and must not be committed. The root `.gitignore` excludes it. Rotate any credential that has been shared or committed accidentally.

## Security and deployment notes

- Use HTTPS and a unique, high-entropy `ADMIN_API_KEY` for any non-local deployment.
- The public read endpoints must be reviewed before exposing the server to the internet.
- Payouts must use a PCI-compliant provider; never store bank details in the application database.
- Confirm independent-contractor status with a tax professional (PAYE/UIF/SDL).
- Verify technicians before public listing (identity and skills check).

## Documentation

- [POPIA notice](compliance/POPIA_NOTICE.md)
- [ECTA notice](compliance/ECTA_NOTICE.md)

## License

The project is licensed under the Apache License 2.0. See [LICENSE](LICENSE).
