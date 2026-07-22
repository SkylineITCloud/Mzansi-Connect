# Mzansi Connect — Agent Context

## READ THIS FIRST
This file is loaded at every prompt. It defines how I must operate. If anything changes (user gives a new rule, format preference, or structural constraint), I update this file immediately.

---

## CRITICAL RULES

### 1. Check context first
Read this file at the start of every single prompt before doing anything else.

### 2. Dual-folder sync (MANDATORY)
Every file edit in `Mzansi Connect/` MUST be mirrored to `C:\Users\pervy\OneDrive\Desktop\Mzansi Connect\Mzansi-Connect\` at the same relative path.
The nested `Mzansi-Connect` folder is the GitHub-published version. The root `Mzansi Connect` is the working copy.
If I create a new file, create it in BOTH folders. If I edit, edit BOTH.

### 3. Single source of truth (for config)
The `.ctx/context.md` in `Mzansi Connect/` is the primary. Mirror edits to `Mzansi-Connect/.ctx/context.md` immediately.

### 4. Product changes → update index.html
Whenever a product status changes (new prototype, launch, feature update, etc.), update the `index.html` website immediately to reflect it. The website is the public face of all products.

### 5. Brand separation
Mzansi Connect is a division of Skyline IT & Cloud. Do NOT confuse it with Circuit Forge Technologies, StudySync, or VALOW. — those are separate Skyline IT subsidiaries.

---

## USER PROFILE

- **Name**: S'nqobile Langa Hlatshwayo
- **Role**: Founder, Skyline IT & Cloud (parent company)
- **Email**: projectpstg@gmail.com
- **Domain**: www.skylineit.site
- **Location**: Durban, South Africa
- **Reference**: Direct, prefers action over explanation

## COMMUNICATION PREFERENCES

- **Tone**: Direct, concise, no fluff. Get to the point.
- **Format**: Lowercase often used. No formal greetings or sign-offs.
- **Length**: Short responses preferred. Don't over-explain unless asked.
- **Corrections**: If told something is wrong, fix it immediately without arguing.
- **Decisions**: When they state something about the project structure, accept it literally.

## PROJECT: Mzansi Connect

Smart Technology for Every South African — IoT solutions focused on townships, SMMEs, schools, landlords, and entrepreneurs.

### CURRENT PRIORITY: Livestock GPS Tracker
This is the company's **flagship product** and main focus. A physical prototype is being built now. The entire website hero, prototype spotlight section (with 4 demo images), and product grid lead with this product. All development, marketing, and content decisions should prioritize it.

### Brand Identity
- **Colours**: SA flag brutalist — green (#007A4B), gold (#FFB612), red (#DE3831), blue (#002395), black (#000000), white (#FFFFFF)
- **Vibe**: Proudly South African, bold, activist, community-first, tech-forward
- **Theme**: Brutalist design with thick black borders, high contrast, flag motifs, bold typography
- **Fonts**: Space Grotesk (body), Archivo Black (headings, uppercase)
- **Logo**: `images/Logo.png` — used in nav, footer, hero, favicon
- **Images**: All images live in `images/` folder (Logo.png, Demo 1.png through Demo 8.png)

### Product Lines (IoT)

| Product | Purpose | Status |
| --- | --- | --- |
| **Livestock GPS Tracker** | **ESP32-based GPS collar for livestock — flagship prototype** | **Prototyping (current focus)** |
| Smart Spaza Shop Security | Motion, door, panic, SMS alerts for small businesses | Concept |
| Smart Water Tank Monitor | Water level, leak, overflow monitoring | Concept |
| Kasi Smart Electricity Monitor | Usage, prepaid meter, appliance tracking | Concept |
| Smart Rental Room Manager | Utility + occupancy monitoring for landlords | Concept |
| Taxi Fleet Tracker | GPS, driver behaviour, fuel monitoring | Concept |

### Livestock GPS Tracker (`C:\Users\pervy\OneDrive\Desktop\Livestock tracker\`)
Full-stack IoT prototype with LoRa sub-collar antitheft mesh. Has its own `.ctx/context.md`.
- **Main firmware**: ESP32 + GPS (NEO-6M/8M) + SIM800L GSM + SX1278 LoRa, deep sleep 5-min, panic detection (high speed, rapid accel, sub-collar events)
- **Sub-collar firmware**: ESP32 + LoRa + strap continuity sensor — detects collar cut/opening, low battery, no-main-contact. Communicates via LoRa 868MHz
- **Panic system**: Severity levels (info/warning/critical), triggers: strap_breach, high_speed, rapid_accel, sub_strayed, sub_low_battery
- **Server**: Express.js, Supabase, helmet/cors/rate-limit, serves web dashboard
- **Database**: Supabase/PostgreSQL + PostGIS — tables: `animals`, `locations`, `geofences`, `sub_collars`, `panic_events`, `alerts`. Views: `active_panics`, `latest_locations`
- **Web dashboard**: Leaflet.js map, realtime Supabase subscriptions, panic bar, animal sidebar, battery/connection status
- **Android app**: Kotlin/Jetpack Compose, osmdroid map, panic indicators, Supabase realtime
- **Hardware**: ESP32 + GPS + SIM800L + SX1278 LoRa + TP4056 + LiPo + solar + strap sensor, ~$43/unit cellular
- **Compliance**: POPIA, ECTA, ICASA notices
- **API**: `POST /api/locations`, `POST /api/panic`, `GET /api/locations/:id`, `GET /api/animals`, `POST /api/animals`, `PUT /api/animals/:id`, `GET /api/latest`, `GET /api/panic/active`, `GET /api/health`
- **Images**: Demo 1-8 show dashboard, map view, analytics, animal list, and additional prototype screenshots

### Groundbreaking Ideas
- Smart Street Pole Network (solar IoT nodes with WiFi + surveillance + emergency)
- Community Mesh Internet (decentralized, community-owned)
- Smart Container Business Hub (solar-powered shipping container centres)
- Smart School Kit (attendance, smart bell, security for schools)

### Ecosystem
- **Mzansi Connect App** — mobile app for device data, alerts, subscriptions
- **Mzansi Cloud** — secure cloud platform for analytics + dashboards
- **Mzansi Marketplace** — platform connecting local technicians to installation jobs

### Price Compare Prototype (`price-compare-app/`)
- Android app (Kotlin, Retrofit) + Node.js/Express API + SQLite (better-sqlite3)
- Backend runs on port 3000, configured via `.env`
- Start with: `cd price-compare-app/backend && npm install && npm run seed && npm start`
- API endpoints: `/api/health`, `/api/auth/*`, `/api/products`, `/api/search?q=...`
- Demo account: thandi@example.com / password123
- JWT auth with bcryptjs, cors, express-validator

### Backend Server (`server/`)
Express.js server powering the main website contact form, newsletter, and admin endpoints.

**Quick start:**
```
start-server.bat
# or: cd server && npm install && npm start
```

| Feature | Details |
| --- | --- |
| Port | 3000 (configurable via `.env`) |
| Database | JSON file storage (`server/data/contacts.json`, `server/data/subscribers.json`) |
| Security | helmet, CORS (origin-locked), input validation (express-validator) |
| Rate limiting | 200 req/15min global, 5 req/hour for contact/subscribe |
| Admin auth | `X-API-Key` header matching `ADMIN_API_KEY` in `.env` |
| Static files | Serves entire repo root (index.html + images/ + assets) |

**API Endpoints:**

| Method | Path | Auth | Purpose |
| --- | --- | --- | --- |
| `POST` | `/api/contact` | Public (rate-limited) | Submit contact form |
| `POST` | `/api/subscribe` | Public (rate-limited) | Subscribe to newsletter |
| `GET` | `/api/admin/stats` | `X-API-Key` | Contact & subscriber counts |
| `GET` | `/api/admin/contacts` | `X-API-Key` | List all contact submissions |
| `GET` | `/api/admin/subscribers` | `X-API-Key` | List all subscribers |
| `GET` | `/api/health` | None | Health check |

### Compliance & Licensing
- `LICENSE` — Apache 2.0
- `compliance/POPIA_NOTICE.md` — POPIA compliance (personal info)
- `compliance/ECTA_NOTICE.md` — ECTA compliance (e-transactions)
- `compliance/ICASA_NOTICE.md` — ICASA compliance (radio spectrum)

### Documentation (`docs/`)
- Business plan, feasibility study, financial projections, market analysis
- Marketing strategy, operational plan, SWOT analysis, technical specification

## CODE STYLE RULES
- No comments in code unless explicitly asked
- Match existing patterns (don't introduce new libraries/frameworks unasked)
- Single-file HTML approach for the website (CSS + JS embedded)
- Keep font sizes readable (at least 0.88rem for body text)
- All images referenced as `images/` path (not root)

## HOW TO HANDLE REQUESTS

1. **Clarify if needed** — if unsure what user means, ask a short question
2. **Make the change** — edit files directly
3. **Mirror** — duplicate to `Mzansi-Connect/` immediately
4. **Verify** — if tests exist or I can quickly check, do so
5. **Be concise** — no long explanations, just confirm what was done

## MIRROR CHECKLIST (run after every change)
- [ ] Did I edit the file in `Mzansi Connect\`?
- [ ] Did I make the SAME edit in `Mzansi-Connect\`?
- [ ] If I created a new file, did I create it in both?
