# Livestock GPS Tracker — Agent Context

## READ THIS FIRST
This file is loaded at every prompt. It defines how I must operate. If anything changes (user gives a new rule, format preference, or structural constraint), I update this file immediately.

---

## CRITICAL RULES

### 1. Check context first
Read this file at the start of every single prompt before doing anything else.

### 2. Single source of truth
This project lives in this folder only. No mirror sync unless explicitly told otherwise.

### 3. Parent company
Livestock GPS Tracker is the flagship product of **Mzansi Connect**, a division of **Skyline IT & Cloud**. Physical prototype being built.

---

## USER PROFILE

- **Name**: S'nqobile Langa Hlatshwayo
- **Role**: Founder, Skyline IT & Cloud (parent company)
- **Email**: projectpstg@gmail.com
- **Domain**: www.skylineit.site
- **Location**: Durban, South Africa
- **Reference**: Direct, prefers action over explanation

## COMMUNICATION PREFERENCES

- **Tone**: Direct, concise, no fluff.
- **Format**: Lowercase often used. No formal greetings or sign-offs.
- **Length**: Short responses preferred. Don't over-explain unless asked.
- **Corrections**: Fix immediately without arguing.
- **Decisions**: Accept literal statements about project structure.

## PROJECT: Livestock GPS Tracker

Internet Of Things (IoT) prototype for monitoring livestock locations using ESP32-based GPS collars with sub-collar antitheft system.

### Status: Prototype (physical build in progress)
Features: GPS tracking, LoRa mesh sub-collars, strap breach detection, panic detection, real-time dashboard, Android app.

### Architecture
```
ESP32 Main Collar (GPS + GSM + LoRa)
        |     ↕ LoRa 868 MHz
        |     ↕ strap/heartbeat
        v     ESP32 Sub-Collars (LoRa + strap sensor)
POST /api/locations (X-API-Key)
        |
Express server + Supabase
        |
Web dashboard (Leaflet)    Android app (Jetpack Compose)
```

### Components

| Component | Location | Technology |
| --- | --- | --- |
| Main firmware | `firmware/tracker.ino` | ESP32 + GPS + GSM + LoRa, deep sleep, panic detection |
| Sub-collar firmware | `firmware/sub-collar.ino` | ESP32 + LoRa + strap sensor, heartbeat, breach alerts |
| API Server | `server/` | Express.js, Supabase JS client, helmet, cors, rate-limit, morgan |
| Database | `database/schema.sql` | Supabase/PostgreSQL, PostGIS, RLS, panic_events |
| Web Dashboard | `web/` | Leaflet map, realtime Supabase, panic bar, animal sidebar |
| Android App | `android/` | Kotlin, Jetpack Compose, osmdroid map, panic support |
| Hardware | `hardware/` | BOM (low-cost cellular), wiring diagram |
| Compliance | `compliance/` | POPIA, ECTA, ICASA notices |

### Firmware — Main Collar (`firmware/tracker.ino`)
- Board: ESP32-WROOM-32
- Libraries: TinyGPSPlus, WiFi, HTTPClient, ArduinoJson, SPI, LoRa
- GPS on UART2, SIM800L GSM on UART1, SX1278 LoRa on SPI
- Deep sleep 5-min intervals
- **Panic detection**: high speed (>25 km/h), rapid acceleration (>15 km/h delta), sub-collar events
- **LoRa mesh**: manages up to 10 sub-collars, tracks heartbeat timeout (5 min), detects strayed subs (>3km distance)
- Sends consolidated location + sub status JSON to server via HTTP POST with X-API-Key

### Firmware — Sub-Collar (`firmware/sub-collar.ino`)
- ESP32 + SX1278 LoRa 868 MHz + strap continuity sensor
- Battery measurement via ADC
- **Triggers**: `strap_breach` (collar cut/opened), `low_battery` (<3.3V), `no_main_contact` (5 min without main)
- Sleeps 30s between cycles, sends heartbeat every 60s
- Acknowledges main collar pings, responds to ACK requests
- SA-compliant 868 MHz ISM band, ICASA notice included

### Config (`firmware/config.example.h` → `config.h`)
- WiFi, server URL, API key, animal UUID
- Sub-collar identity (SUB_COLLAR_ID, MAIN_COLLAR_ID)
- Panic thresholds: PANIC_SPEED_KMH (25), PANIC_ACCEL_KMH (15)
- Sub-collar: SUB_TIMEOUT_MS (5 min), SUB_STRAY_KM (3 km), MAX_SUBS (10)
- Pin definitions: GPS, GSM, LoRa, strap sensor, battery

### Server (`server/index.js`)
- Express.js on port 3000, `.env` config
- Security: helmet, CORS (origin-locked), rate limiting (200/15min global, 30/min ingest)
- Auth: `requireApiKey` middleware for collar endpoints
- Serves web dashboard static files + `/config.js` for Supabase anon key
- Supabase service-role client for DB ops

### API Endpoints

| Method | Path | Auth | Purpose |
| --- | --- | --- | --- |
| `POST` | `/api/locations` | `X-API-Key` | Store collar GPS ping + sub-collar status |
| `POST` | `/api/panic` | `X-API-Key` | Report panic event (high speed, strap breach, etc.) |
| `GET` | `/api/locations/:animalId?limit=50` | None | Recent positions for one animal |
| `GET` | `/api/animals` | None | List active animals |
| `POST` | `/api/animals` | `X-API-Key` | Register an animal |
| `PUT` | `/api/animals/:id` | `X-API-Key` | Update animal metadata |
| `GET` | `/api/latest` | None | Latest position per animal |
| `GET` | `/api/panic/active` | None | Active (unresolved) panic events |
| `GET` | `/api/health` | None | Health check |

### Database (`database/schema.sql`)
- Supabase/PostgreSQL with PostGIS + pgcrypto
- **Tables**: `animals`, `locations` (GEOGRAPHY point), `geofences` (polygon), `sub_collars` (linked to main animal), `panic_events` (severity levels, sub_collar_id, trigger types), `alerts` (legacy, linked to panic_events)
- **Triggers**: speed (>25), rapid accel (>15), off-hours, geofence breach, sub-collar strap breach, strayed, low battery
- **Views**: `active_panics` (unresolved + animal details), `latest_locations` (DISTINCT ON per animal)
- **Indexes**: `idx_locations_animal_ts`, `idx_locations_geog` (GIST), `idx_panic_events_animal`, `idx_panic_events_unresolved` (partial)
- RLS on all tables, service-role bypass for inserts

### Web Dashboard (`web/`)
- Leaflet.js map with custom markers (selected, default, panic-pulsing)
- Real-time via Supabase postgres_changes subscriptions
- Panic alert bar at top (pulsing red items, severity levels)
- Animal sidebar with tag, species, speed, battery, panic badges
- Connection status indicator, average battery, active count
- Dark theme (#1a1a2e, #16213e, #e94560 accents)

### Android App (`android/`)
- Kotlin, Jetpack Compose, Gradle KTS
- osmdroid map view with animal markers (panic markers highlighted)
- Animal list with panic indicators (red cards, 🚨 badges, yellow text)
- Supabase via `SupabaseRepo` singleton: `getLatestLocations()`, `getActivePanics()`, realtime subscriptions
- Data models: `Animal` (id, tag, species, lat/lng, speed, battery), `Location`, `PanicEvent`
- Config via `local.properties` → BuildConfig (SUPABASE_URL, SUPABASE_ANON_KEY)

### Hardware (`hardware/parts-list.md`)
- ESP32 ($4.50), NEO-6M/8M GPS ($6.00), SIM800L GSM ($7.00), SX1278 LoRa ($3.00)
- TP4056 charger, 5000mAh LiPo ($12.00), 1W solar panel ($3.50), IP67 enclosure ($5.00)
- Strap continuity sensor (wire + resistor, ~$0.50)
- **Total (cellular):** under R800/unit | **Bulk (1000):** volume pricing

## CODE STYLE RULES
- No comments in code unless explicitly asked
- Match existing patterns (don't introduce new libraries/frameworks unasked)
- Keep fonts readable (at least 0.88rem)

## HOW TO HANDLE REQUESTS

1. **Clarify if needed** — short question if unclear
2. **Make the change** — edit files directly
3. **Verify** — if tests exist or I can quickly check, do so
4. **Be concise** — no long explanations, just confirm what was done
