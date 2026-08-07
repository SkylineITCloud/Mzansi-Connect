# Livestock Tracker

An Internet Of Things (IoT) prototype for monitoring livestock locations. An ESP32-based collar sends GPS positions to the unified Skyline IT API. The repository also includes a browser dashboard and an Android client for viewing tracked animals.

> Status: prototype. Review security, hardware safety, data protection, and radio-approval requirements before any production or commercial deployment.

## Components

| Area | Purpose | Location |
| --- | --- | --- |
| Firmware | ESP32 GPS collar that posts location pings | `firmware/` |
| API routes | Unified Skyline IT server mounts the tracker API at `/api/mzansi/livestock/*` | `server/routes/mzansiLivestock.js` |
| Data store | Local JSON files (animals, locations, panic events, alerts) | `data/mzansi/livestock/` |
| Database schema | Original PostgreSQL schema kept for reference | `database/schema.sql` |
| Web dashboard | Map-based browser interface (Leaflet vendored in `web/vendor/`) | `web/` |
| Android app | Kotlin Android client | `android/` |
| Hardware | Bill of materials and collar wiring schematic | `hardware/` |
| Compliance and legal | South African compliance notes and third-party notices | `compliance/`, `legal/` |

## Architecture

```text
ESP32 collar + GPS
        |
        | POST /api/mzansi/livestock/locations (X-API-Key)
        v
Unified Skyline IT server (port 3000)
        |
        +--> /api/mzansi/livestock/*  JSON-backed API
        +--> /livestock/              Web dashboard (static)
```

## Prerequisites

- Node.js 18 or later and npm
- The unified Skyline IT server (`server/index.js`) with its dependencies installed
- Arduino IDE 2.x with an ESP32 board package
- An ESP32, compatible GPS module, and the parts listed in [hardware/parts-list.md](hardware/parts-list.md)
- Android Studio and JDK 17 for the Android app

## Quick start

### 1. Start the unified server

```bash
npm install
node server/index.js
```

The dashboard is served at `http://localhost:3000/livestock/` and the API at `http://localhost:3000/api/mzansi/livestock/`. Seed data (animals and locations) is created automatically in `data/mzansi/livestock/` by `server/scripts/seedProducts.js`.

Write endpoints (`/api/mzansi/livestock/locations`, `/panic`, `/panic/:id/resolve`, `/animals`) require the `X-Api-Key` header with `ADMIN_API_KEY` from the root `.env`. Use an existing animal id (e.g. `animal-1`) as the collar's `ANIMAL_ID`.

### 2. Configure the firmware

```bash
cd firmware
cp config.example.h config.h
```

Edit the local `config.h` with Wi-Fi credentials, the server URL, `COLLAR_API_KEY` (use the server's `ADMIN_API_KEY`), and the animal id. Then open `tracker.ino` in Arduino IDE, install the libraries named in its header (`TinyGPSPlus` and `ArduinoJson`), select the ESP32 board and port, and upload it.

Use [hardware/wiring-diagram.svg](hardware/wiring-diagram.svg) and [hardware/parts-list.md](hardware/parts-list.md) when assembling the collar.

### 3. Run the web dashboard

With the server running, open `http://localhost:3000/livestock/` in a browser. Leaflet is vendored locally, so the dashboard also works offline.

### 4. Configure the Android application

```bash
cd android
cp local.properties.example local.properties
```

Configure the project for your own backend, then open the `android/` directory in Android Studio and build or run the application.

## API summary

| Method | Path | Authentication | Purpose |
| --- | --- | --- | --- |
| `POST` | `/locations` | `X-API-Key` | Store a collar GPS ping |
| `POST` | `/panic` | `X-API-Key` | Ingest a panic event from a collar |
| `GET` | `/panic/active` | None | Active (unresolved) panic events |
| `PUT` | `/panic/:id/resolve` | `X-API-Key` | Resolve a panic event |
| `GET` | `/locations/:animalId?limit=50` | None | Retrieve recent positions for one animal |
| `GET` | `/animals` | None | List active animals |
| `POST` | `/animals` | `X-API-Key` | Register an animal |
| `PUT` | `/animals/:id` | `X-API-Key` | Update an animal |
| `GET` | `/latest` | None | Retrieve the latest position per animal |
| `GET` | `/health` | None | Server health check |

All paths are prefixed with `/api/mzansi/livestock` on the unified server. Example location payload:

```json
{
  "animal_id": "animal-1",
  "latitude": -29.8500,
  "longitude": 30.9800,
  "speed_kmh": 0.0,
  "battery_v": 3.9
}
```

## Configuration and secrets

The unified server's root `.env` file contains environment-specific values (`ADMIN_API_KEY`, `JWT_SECRET`, etc.) and must not be committed. Local copies of `firmware/config.h` and `android/local.properties` hold device-side values. The root `.gitignore` excludes these files. Rotate any credential that has been shared or committed accidentally.

## Security and deployment notes

- Use HTTPS and a unique, high-entropy `ADMIN_API_KEY` for any non-local deployment.
- The public read endpoints must be reviewed before exposing the server to the internet.
- Confirm local requirements for radio equipment, SIM provisioning, enclosure safety, and personal-information handling before field deployment.

## Documentation

- [Hardware parts list](hardware/parts-list.md)
- [Wiring diagram](hardware/wiring-diagram.svg)
- [POPIA notice](compliance/POPIA_NOTICE.md)
- [ECTA notice](compliance/ECTA_NOTICE.md)
- [ICASA notice](compliance/ICASA_NOTICE.md)
- [Third-party notices](legal/THIRD_PARTY_NOTICES.md)

## License

The project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) and [legal/THIRD_PARTY_NOTICES.md](legal/THIRD_PARTY_NOTICES.md).
