# Mzansi Price Compare

An Android prototype for comparing product prices across South African stores. Users can search the demo catalogue, filter results, view store listings, register or sign in, and save products.

## Architecture

| Component | Location | Technology |
| --- | --- | --- |
| Android client | [`android/`](android/) | Kotlin, Android Views, Retrofit, Material Design |
| REST API | [`backend/`](backend/) | Node.js, Express, SQLite, JWT |

## Prerequisites

- Node.js and npm for the backend.
- Android Studio with an Android SDK for the mobile app.
- An Android emulator or device running Android 8.0 (API 26) or newer.

## Run locally

### 1. Start the API

```powershell
Set-Location backend
npm install
npm run seed
npm start
```

The API starts on `http://localhost:3000`. Confirm it is running with:

```powershell
Invoke-RestMethod http://localhost:3000/api/health
```

Seeding resets the local demo data; run it before first use or whenever a fresh demo database is needed.

### 2. Build and run the Android client

1. Open the [`android/`](android/) folder in Android Studio.
2. Allow Gradle to sync the project.
3. Choose an emulator or connected device and run the `app` configuration.

The app defaults to `http://10.0.2.2:3000/`, which maps an Android emulator to the host computer. For a physical device, update `BASE_URL` in [`RetrofitClient.kt`](android/app/src/main/java/com/mzansi/pricecompare/api/RetrofitClient.kt) to the computer's LAN address, for example `http://192.168.1.10:3000/`. Ensure the device and computer share the same network.

## Demo account

| Email | Password |
| --- | --- |
| `thandi@example.com` | `password123` |

## API overview

| Endpoint | Description |
| --- | --- |
| `GET /api/health` | API health check. |
| `POST /api/auth/register` | Create an account. |
| `POST /api/auth/login` | Authenticate and receive a JWT. |
| `GET /api/products` | Browse products; supports paging and category filtering. |
| `GET /api/products/:id` | View a product and its store listings. |
| `GET /api/search?q=...` | Search products by name or brand. |

Authenticated product-saving and profile endpoints are also available. See [`backend/README.md`](backend/README.md) for API setup details and [`android/README.md`](android/README.md) for Android-specific notes.

## Notes

This is a prototype with local SQLite demo data. It is not configured for production deployment, real retailer feeds, or production-grade secret management.
