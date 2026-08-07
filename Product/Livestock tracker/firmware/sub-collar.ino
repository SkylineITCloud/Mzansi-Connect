/*
  LiveStock GPS Tracker — Sub-Collar Firmware
  SPDX-License-Identifier: Apache-2.0

  Hardware: ESP32 + SX1278 LoRa + strap continuity sensor
  Communicates with main collar via LoRa.

  Panic triggers:
    - strap_breach:   collar opened/cut (continuity broken)
    - low_battery:    battery voltage critical
    - no_main_contact: hasn't heard from main collar recently (going solo)

  The main collar aggregates sub-collar status and reports to server.

  Wiring:
    LoRa NSS  → GPIO5
    LoRa RST  → GPIO14
    LoRa DIO0 → GPIO2
    LoRa SCK  → GPIO18
    LoRa MOSI → GPIO23
    LoRa MISO → GPIO19
    Strap sensor → GPIO33 (INPUT_PULLUP; LOW=buckled, HIGH=cut)
*/

#include <SPI.h>
#include <LoRa.h>
#include "config.h"

// ─── Sub-collar identity (set per device in config.h) ───────
const char* SUB_COLLAR_ID = SUB_COLLAR_ID;  // from config.h
const char* MAIN_COLLAR_ID = MAIN_COLLAR_ID;

// ─── LoRa pins ──────────────────────────────────────────────
#define LORA_NSS  5
#define LORA_RST  14
#define LORA_DIO0 2
#define LORA_SCK  18
#define LORA_MOSI 23
#define LORA_MISO 19

// ─── Strap sensor ───────────────────────────────────────────
#define STRAP_PIN     33
#define BATTERY_PIN   34

// ─── Timing ─────────────────────────────────────────────────
const unsigned long CHECK_INTERVAL = 60000;      // send heartbeat every 60s
const unsigned long STRAP_DEBOUNCE = 3000;        // 3s debounce for strap
const unsigned long MAIN_TIMEOUT   = 300000;      // 5 min without main = solo panic
const unsigned long SLEEP_INTERVAL = 30;          // 30s deep sleep between cycles

unsigned long lastMainHeard = 0;
unsigned long lastCheckin = 0;
bool strapOk = false;
bool lastStrapState = true;
unsigned long strapChangeTime = 0;

float readBattery() {
  int raw = analogRead(BATTERY_PIN);
  return (raw * ADC_REF) / ADC_RES * VOLTAGE_DIVIDER;
}

bool checkStrap() {
  // LOW = buckled (intact), HIGH = cut/opened
  return digitalRead(STRAP_PIN) == LOW;
}

void sendLoRa(const char* type, const char* value) {
  LoRa.beginPacket();
  LoRa.print(SUB_COLLAR_ID);
  LoRa.print("|");
  LoRa.print(type);
  LoRa.print("|");
  LoRa.print(value);
  LoRa.print("|");
  LoRa.print(readBattery());
  LoRa.endPacket();
}

void setup() {
  Serial.begin(115200);
  pinMode(STRAP_PIN, INPUT_PULLUP);
  pinMode(BATTERY_PIN, INPUT);

  // Init LoRa
  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_NSS);
  LoRa.setPins(LORA_NSS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(868E6)) {  // 868 MHz ISM (SA-compliant)
    Serial.println("LoRa init failed");
    while (1);
  }
  LoRa.setTxPower(20);     // max power for 3km+ range
  LoRa.setSpreadingFactor(10);
  LoRa.setCodingRate4(5);
  LoRa.enableCrc();

  float battery = readBattery();
  Serial.printf("Sub-collar %s boot. Battery: %.2fV\n", SUB_COLLAR_ID, battery);

  strapOk = checkStrap();
  lastStrapState = strapOk;
  lastMainHeard = millis();

  // Immediate strap check on boot
  if (!strapOk) {
    Serial.println("STRAP BREACH on boot!");
    sendLoRa("strap_breach", "opened_on_boot");
  }

  // Low battery check
  if (battery < 3.3) {
    sendLoRa("low_battery", String(battery, 2).c_str());
  }

  // Listen for main collar acknowledgment briefly
  unsigned long listenStart = millis();
  while (millis() - listenStart < 5000) {
    int packetSize = LoRa.parsePacket();
    if (packetSize) {
      String incoming = "";
      while (LoRa.available()) incoming += (char)LoRa.read();
      if (incoming.startsWith("MAIN|ACK|")) {
        lastMainHeard = millis();
        Serial.println("Acknowledged by main collar");
      }
      if (incoming.startsWith("MAIN|PING|")) {
        lastMainHeard = millis();
        // Respond to main's ping
        sendLoRa("heartbeat", "alive");
      }
    }
  }

  // Send initial heartbeat with strap status
  sendLoRa("heartbeat", strapOk ? "alive" : "breached");
  lastCheckin = millis();

  Serial.println("Entering deep sleep cycle");
  esp_sleep_enable_timer_wakeup(SLEEP_INTERVAL * 1000000ULL);
  esp_deep_sleep_start();
}

void loop() {}
