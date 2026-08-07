// ============================================================
// LiveStock GPS Tracker — Firmware Configuration Template
// SPDX-License-Identifier: Apache-2.0
//
// ⚠ Copy this file to config.h and fill in your real values.
//    config.h is in .gitignore — NEVER commit it.
//
// POPIA: No personal information stored in firmware.
// ICASA: WiFi BLE GSM modules approved for SA use.
// ============================================================

#ifndef CONFIG_H
#define CONFIG_H

// ─── WiFi ───────────────────────────────────────────────────
const char* WIFI_SSID       = "YourWiFiSSID";
const char* WIFI_PASSWORD   = "YourWiFiPassword";

// ─── Server ─────────────────────────────────────────────────
const char* SERVER_URL      = "http://your-server-ip:3000/api/locations";
const char* API_KEY         = "changeme-replace-with-real-api-key";

// ─── Animal ─────────────────────────────────────────────────
const char* ANIMAL_ID       = "00000000-0000-0000-0000-000000000000";

// ─── Sub-collar identity (set one per sub device) ───────────
// The main collar uses these to identify which subs belong to it.
const char* SUB_COLLAR_ID = "SUB-001";       // change per sub-collar
const char* MAIN_COLLAR_ID = "MAIN-001";      // main collar ID

// ─── Panic detection thresholds ─────────────────────────────
const float PANIC_SPEED_KMH   = 25.0;
const float PANIC_ACCEL_KMH   = 15.0;

// ─── Sub-collar tracking ────────────────────────────────────
const unsigned long SUB_TIMEOUT_MS = 300000;   // 5 min without LoRa = strayed
const float SUB_STRAY_KM = 3.0;                // distance threshold for stray alert
const int MAX_SUBS = 10;                       // max sub-collars per main

// ─── Hardware pins (main collar) ────────────────────────────
#define GPS_RX_PIN  16
#define GPS_TX_PIN  17
#define GSM_RX_PIN  18
#define GSM_TX_PIN  19
#define BATTERY_PIN 34
#define WAKE_PIN    13
#define MAIN_STRAP_PIN 32     // continuity loop through main collar strap

// ─── LoRa module pins (SX1278) ─────────────────────────────
#define LORA_NSS  5
#define LORA_RST  14
#define LORA_DIO0 2
#define LORA_SCK  18
#define LORA_MOSI 23
#define LORA_MISO 19

// ─── Sub-collar strap sensor pin ───────────────────────────
#define STRAP_PIN 33          // continuity loop through sub-collar strap

// ─── Battery measurement ────────────────────────────────────
const float VOLTAGE_DIVIDER = 2.0;
const float ADC_REF = 3.3;
const int ADC_RES = 4095;

#endif
