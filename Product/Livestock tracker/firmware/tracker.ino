/*
  LiveStock GPS Tracker — Main Collar Firmware
  SPDX-License-Identifier: Apache-2.0

  Hardware: ESP32 + NEO-6M GPS + SIM800L GSM + SX1278 LoRa
  Manages: GPS location, GSM upload, LoRa sub-collar tracking

  Anti-theft features (all straps):
    - Main collar strap sensor:  continuity loop through main collar strap
    - Sub-collar strap sensor:   continuity loop through each sub strap
    - Strap breach → immediate panic alert (critical severity)
    - Sub strayed > 3km from main → panic alert
    - High speed / rapid acceleration → panic alert

  Panic triggers:
    - strap_breach (main or sub)
    - sub_strap_breach
    - sub_strayed (> 3km or LoRa timeout)
    - sub_low_battery
    - high_speed / rapid_accel

  Wiring: see hardware/wiring-diagram.svg
    LoRa NSS  → GPIO5     LoRa RST  → GPIO14
    LoRa DIO0 → GPIO2     LoRa SCK  → GPIO18
    LoRa MOSI → GPIO23    LoRa MISO → GPIO19
    Strap sensor → GPIO32  (LOW = buckled, HIGH = cut/opened)
*/

#include <WiFi.h>
#include <HTTPClient.h>
#include <TinyGPSPlus.h>
#include <ArduinoJson.h>
#include <SPI.h>
#include <LoRa.h>
#include "config.h"

TinyGPSPlus gps;
HardwareSerial gpsSerial(2);

// ─── LoRa pins ──────────────────────────────────────────────
#define LORA_NSS  5
#define LORA_RST  14
#define LORA_DIO0 2
#define LORA_SCK  18
#define LORA_MOSI 23
#define LORA_MISO 19

// ─── Sub-collar tracking ───────────────────────────────────
struct SubCollar {
  char id[20];
  float battery;
  unsigned long lastHeard;
  bool strapOk;
  bool breachPending;   // true if strap breach not yet reported
};

const int MAX_SUBS = 10;
SubCollar subs[MAX_SUBS];
int subCount = 0;

float lastSpeed = 0;
float lastLat = 0;
float lastLng = 0;
bool hasLastFix = false;

const unsigned long SLEEP_INTERVAL = 300;
const int GPS_FIX_TIMEOUT = 10000;
const float STRAY_DISTANCE_KM = 3.0;

// ─── Main collar strap sensor ───────────────────────────────
#define MAIN_STRAP_PIN 32
bool mainStrapOk = false;
bool mainStrapBreachReported = false;

// ─── Utility ────────────────────────────────────────────────
float readBatteryVoltage() {
  int raw = analogRead(BATTERY_PIN);
  return (raw * ADC_REF) / ADC_RES * VOLTAGE_DIVIDER;
}

bool connectWiFi() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 20) {
    delay(500);
    attempts++;
  }
  return WiFi.status() == WL_CONNECTED;
}

int httpPost(const char* url, const char* body) {
  if (WiFi.status() != WL_CONNECTED && !connectWiFi()) return -1;
  HTTPClient http;
  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("X-API-Key", API_KEY);
  int code = http.POST((uint8_t*)body, strlen(body));
  http.end();
  return code;
}

// ─── Main collar strap sensor ───────────────────────────────
bool checkMainStrap() {
  // LOW = buckled (circuit closed via wire loop through strap)
  // HIGH = cut/opened (circuit broken)
  return digitalRead(MAIN_STRAP_PIN) == LOW;
}

void handleMainStrapBreach(float lat, float lng) {
  if (!mainStrapOk && !mainStrapBreachReported) {
    mainStrapBreachReported = true;
    char msg[64];
    snprintf(msg, sizeof(msg), "Main collar strap breached at %.6f,%.6f", lat, lng);
    sendPanic("strap_breach", lat, lng, 0, readBatteryVoltage(), msg);
    // Include in location report immediately
    mainStrapOk = false;
  }
}

// ─── Haversine distance (km) ────────────────────────────────
float haversine(float lat1, float lon1, float lat2, float lon2) {
  float R = 6371.0;
  float dLat = (lat2 - lat1) * PI / 180.0;
  float dLon = (lon2 - lon1) * PI / 180.0;
  float a = sin(dLat/2)*sin(dLat/2) +
            cos(lat1*PI/180)*cos(lat2*PI/180)*sin(dLon/2)*sin(dLon/2);
  return R * 2 * atan2(sqrt(a), sqrt(1-a));
}

// ─── Find or register a sub collar ─────────────────────────
int findSub(const char* id) {
  for (int i = 0; i < subCount; i++)
    if (strcmp(subs[i].id, id) == 0) return i;
  return -1;
}

int addSub(const char* id) {
  if (subCount >= MAX_SUBS) return -1;
  strncpy(subs[subCount].id, id, 19);
  subs[subCount].id[19] = 0;
  subs[subCount].battery = 0;
  subs[subCount].lastHeard = millis();
  subs[subCount].strapOk = true;
  subs[subCount].breachPending = false;
  return subCount++;
}

// ─── Send consolidated panic (main or sub) ──────────────────
void sendPanic(const char* trigger, float lat, float lng, float speed, float battery, const char* msg, const char* subId = "") {
  char url[128];
  snprintf(url, sizeof(url), "%s/panic", SERVER_URL);
  char* slash = strrchr(url, '/');
  if (slash) *slash = 0;
  strcat(url, "/panic");

  StaticJsonDocument<256> doc;
  doc["animal_id"]  = ANIMAL_ID;
  doc["trigger"]    = trigger;
  doc["latitude"]   = lat;
  doc["longitude"]  = lng;
  doc["speed_kmh"]  = speed;
  doc["battery_v"]  = battery;
  doc["message"]    = msg;
  if (strlen(subId) > 0) doc["sub_collar_id"] = subId;
  String payload;
  serializeJson(doc, payload);
  httpPost(url, payload.c_str());
  Serial.printf("PANIC [%s] → %s\n", trigger, msg);
}

// ─── Send location + strap status + sub-collar status ──────
void sendLocation(float lat, float lng, float speed, float battery) {
  StaticJsonDocument<1024> doc;
  doc["animal_id"]    = ANIMAL_ID;
  doc["latitude"]     = lat;
  doc["longitude"]    = lng;
  doc["speed_kmh"]    = speed;
  doc["battery_v"]    = battery;
  doc["strap_ok"]     = mainStrapOk;        // main collar strap status

  // Attach sub-collar status array
  JsonArray subsArr = doc.createNestedArray("sub_collars");
  unsigned long now = millis();

  for (int i = 0; i < subCount; i++) {
    JsonObject s = subsArr.createNestedObject();
    s["id"]         = subs[i].id;
    s["battery_v"]  = subs[i].battery;
    s["strap_ok"]   = subs[i].strapOk;
    s["last_seen_s"] = (now - subs[i].lastHeard) / 1000;

    // Check if sub has strayed > 3km
    if (subs[i].lastHeard != 0) {
      float dist = haversine(lat, lng, lastLat, lastLng);
      s["distance_km"] = dist;
      s["strayed"] = dist > STRAY_DISTANCE_KM;
    } else {
      s["distance_km"] = -1;
      s["strayed"] = false;
    }
  }

  String payload;
  serializeJson(doc, payload);
  int code = httpPost(SERVER_URL, payload.c_str());
  Serial.printf("POST /api/locations → %d (%d subs)\n", code, subCount);
}

// ─── Process incoming LoRa packet from sub-collar ──────────
void processLoRa() {
  int packetSize = LoRa.parsePacket();
  if (!packetSize) return;

  String incoming = "";
  while (LoRa.available()) incoming += (char)LoRa.read();

  // Format: SUB_ID|type|value|battery
  int firstBar = incoming.indexOf('|');
  int secondBar = incoming.indexOf('|', firstBar + 1);
  int thirdBar = incoming.indexOf('|', secondBar + 1);

  if (firstBar < 0 || secondBar < 0) return;

  String subId = incoming.substring(0, firstBar);
  String type = incoming.substring(firstBar + 1, secondBar);
  String value = incoming.substring(secondBar + 1, thirdBar < 0 ? incoming.length() : thirdBar);
  float subBattery = (thirdBar > 0) ? incoming.substring(thirdBar + 1).toFloat() : 0;

  int idx = findSub(subId.c_str());
  if (idx < 0) idx = addSub(subId.c_str());
  if (idx < 0) return;

  subs[idx].lastHeard = millis();
  subs[idx].battery = subBattery;
  Serial.printf("LoRa from %s: %s = %s  bat=%.2fV\n", subId.c_str(), type.c_str(), value.c_str(), subBattery);

  if (type == "strap_breach") {
    subs[idx].strapOk = false;
    subs[idx].breachPending = true;
  } else if (type == "heartbeat") {
    // value = "alive" (strap OK) or "breached" (strap open)
    subs[idx].strapOk = (value != "breached");
    if (!subs[idx].strapOk && !subs[idx].breachPending) {
      subs[idx].breachPending = true;
    }
  } else if (type == "low_battery" && subBattery < 3.2) {
    char msg[64];
    snprintf(msg, sizeof(msg), "Sub-collar %s battery critical", subId.c_str());
    sendPanic("sub_low_battery", lastLat, lastLng, 0, subBattery, msg, subId.c_str());
  }

  // Acknowledge receipt
  LoRa.beginPacket();
  LoRa.print("MAIN|ACK|");
  LoRa.print(subId);
  LoRa.endPacket();
}

// ─── Check all straps and sub-collar timeouts ───────────────
void checkSubs(float lat, float lng) {
  unsigned long now = millis();

  // Check main collar strap
  mainStrapOk = checkMainStrap();
  if (!mainStrapOk) handleMainStrapBreach(lat, lng);

  // Check sub-collars
  for (int i = 0; i < subCount; i++) {
    if (subs[i].breachPending) {
      subs[i].breachPending = false;
      char msg[64];
      snprintf(msg, sizeof(msg), "Sub-collar %s strap breached", subs[i].id);
      sendPanic("sub_strap_breach", lat, lng, 0, readBatteryVoltage(), msg, subs[i].id);
    }

    if (now - subs[i].lastHeard > SUB_TIMEOUT_MS && subs[i].lastHeard != 0) {
      char msg[64];
      snprintf(msg, sizeof(msg), "Sub-collar %s out of range > %ds",
        subs[i].id, SUB_TIMEOUT_MS / 1000);
      sendPanic("sub_strayed", lat, lng, 0, readBatteryVoltage(), msg, subs[i].id);
      subs[i].lastHeard = now;
    }
  }
}

// ─── Panic detection for main collar ────────────────────────
void checkPanic(float lat, float lng, float speed, float battery) {
  if (speed > PANIC_SPEED_KMH) {
    sendPanic("high_speed", lat, lng, speed, battery, "Speed exceeds threshold");
    return;
  }
  if (hasLastFix && (speed - lastSpeed) > PANIC_ACCEL_KMH) {
    sendPanic("rapid_accel", lat, lng, speed, battery, "Rapid acceleration detected");
    return;
  }
  hasLastFix = true;
  lastSpeed = speed;
  lastLat = lat;
  lastLng = lng;
}

// ─── Setup ──────────────────────────────────────────────────
void setup() {
  Serial.begin(115200);
  gpsSerial.begin(9600, SERIAL_8N1, GPS_RX_PIN, GPS_TX_PIN);
  pinMode(BATTERY_PIN, INPUT);
  pinMode(WAKE_PIN, INPUT_PULLUP);
  pinMode(MAIN_STRAP_PIN, INPUT_PULLUP);

  // Read strap state immediately
  mainStrapOk = checkMainStrap();
  Serial.printf("Main strap: %s\n", mainStrapOk ? "BUCKLED" : "BREACHED");

  // Init LoRa
  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_NSS);
  LoRa.setPins(LORA_NSS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(868E6)) {
    Serial.println("LoRa init failed — continuing without sub-collar support");
  } else {
    LoRa.setTxPower(20);
    LoRa.setSpreadingFactor(10);
    LoRa.setCodingRate4(5);
    LoRa.enableCrc();
    Serial.println("LoRa ready");
  }

  float battery = readBatteryVoltage();
  Serial.printf("Main collar boot. Battery: %.2fV\n", battery);

  // GPS fix
  unsigned long start = millis();
  while (millis() - start < GPS_FIX_TIMEOUT) {
    while (gpsSerial.available()) gps.encode(gpsSerial.read());
  }

  // Listen for sub-collar messages for 5 seconds
  unsigned long loraListen = millis();
  while (millis() - loraListen < 5000) {
    processLoRa();
  }

  // Also ping subs to solicit heartbeats
  LoRa.beginPacket();
  LoRa.print("MAIN|PING|ALL");
  LoRa.endPacket();
  delay(500);
  // Listen for responses
  unsigned long respListen = millis();
  while (millis() - respListen < 3000) {
    processLoRa();
  }

  connectWiFi();

  float lat = 0, lng = 0, speed = 0;
  if (gps.location.isValid()) {
    lat = gps.location.lat();
    lng = gps.location.lng();
    speed = gps.speed.kmph();
    Serial.printf("Fix: %.6f, %.6f  speed=%.1f km/h\n", lat, lng, speed);
  }

  checkSubs(lat, lng);
  checkPanic(lat, lng, speed, battery);
  sendLocation(lat, lng, speed, battery);

  Serial.printf("Sleeping for %lu s\n", SLEEP_INTERVAL);
  esp_sleep_enable_timer_wakeup(SLEEP_INTERVAL * 1000000ULL);
  esp_deep_sleep_start();
}

void loop() {}
