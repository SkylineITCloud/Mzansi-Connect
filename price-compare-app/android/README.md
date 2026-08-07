# Mzansi Price Compare Android app

The Android client for the Mzansi Price Compare prototype is a Kotlin application using XML layouts, Android View Binding, Retrofit, and Material Design components.

## Requirements

- Android Studio.
- JDK 17 (used by the Gradle build).
- Android SDK 34 to compile the app.
- An emulator or device running Android 8.0 / API 26 or newer.

## Open and run

1. Open this `android` folder in Android Studio.
2. Let Gradle finish syncing dependencies.
3. Start the backend API as described in the [backend README](../backend/README.md).
4. Select an emulator or connected device, then run the `app` configuration.

## API connection

The API base URL is defined as `BASE_URL` in [`RetrofitClient.kt`](app/src/main/java/com/mzansi/pricecompare/api/RetrofitClient.kt).

| Target | Base URL |
| --- | --- |
| Android emulator | `http://10.0.2.2:3000/` |
| Physical device | `http://<computer-LAN-IP>:3000/` |

For a physical device, replace the base URL with the computer's LAN IP address and make sure both devices are on the same network. If the connection fails, check the computer firewall and confirm that the API is running.

## Project details

- Package: `com.mzansi.pricecompare`
- Minimum SDK: 26
- Target / compile SDK: 34
- Version: 1.0 (`versionCode` 1)

See the [prototype README](../README.md) for the product overview and demo credentials.
