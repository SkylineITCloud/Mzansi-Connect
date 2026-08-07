# Technical Specification — Mzansi Connect IoT Products

**A subsidiary of Skyline IT & Cloud**  
**Date:** June 2026  
**Version:** 1.0

---

## 1. Hardware Platform

### 1.1 Core Controller: ESP32-WROOM-32

| Specification | Detail |
|--------------|--------|
| Microcontroller | Xtensa dual-core LX6 @ 240MHz |
| RAM | 520 KB SRAM + 4 MB PSRAM |
| Flash | 16 MB |
| WiFi | 802.11 b/g/n (2.4 GHz) |
| Bluetooth | BLE 4.2 |
| GPIO | 25 available pins |
| ADC | 12-bit, 18 channels |
| Power | 3.3V, deep sleep: 5µA |
| Operating Temp | -40°C to +85°C |
| Cost | R80–R120 (retail) |

### 1.2 Connectivity Modules

| Module | Protocol | Range | Power | Cost |
|--------|----------|-------|-------|------|
| SX1278 (LoRa) | LoRaWAN | 2–5 km (line of sight) | 120mA TX | R60–R90 |
| SIM800L | GSM/GPRS (2G) | Unlimited | 500mA burst | R120–R180 |
| SIM7000G | LTE-M / NB-IoT | Unlimited | 350mA burst | R200–R300 |

### 1.3 Sensors & Actuators

| Sensor | Use Case | Interface | Cost |
|--------|----------|-----------|------|
| HC-SR501 PIR | Motion detection | Digital GPIO | R25 |
| Magnetic reed switch | Door/window open/close | Digital GPIO | R18 |
| HC-SR04 Ultrasonic | Water level (tank) | GPIO trigger/echo | R30 |
| Water flow sensor | Leak detection | Pulse counter | R45 |
| ACS712 Current sensor | Electricity monitoring | Analog (ADC) | R40 |
| PZEM-004T Energy monitor | Prepaid meter monitoring | UART/Modbus | R95 |
| NEO-6M GPS | Vehicle tracking | UART | R85 |
| DHT22 | Temp/humidity (school kit) | OneWire | R35 |

---

## 2. Product Specifications

### 2.1 Smart Spaza Shop Security System

| Item | Detail |
|------|--------|
| **Model** | MZ-SEC-100 |
| **Target Price** | R899 |
| **Microcontroller** | ESP32-WROOM-32 |
| **Connectivity** | WiFi + GSM (SIM800L) |
| **Sensors** | PIR motion, magnetic door sensor |
| **Outputs** | Piezo buzzer, LED strip, SMS alert |
| **Power** | 5V DC adapter (included) + battery backup (3.7V 18650) |
| **Battery Life** | 12 hours on backup |
| **Enclosure** | IP54 ABS plastic 3D-printed |
| **Dimensions** | 120 x 80 x 40mm |
| **Alerts** | SMS + Push notification + Buzzer |
| **Panic Button** | External wired button included |

**Firmware Features:**
- Motion detection with configurable sensitivity
- Door open/close detection
- Configurable alert recipients (up to 3 numbers)
- Power outage detection and alert
- Panic button with immediate SMS
- OTA firmware updates over WiFi
- Low battery notification

### 2.2 Smart Water Tank Monitor

| Item | Detail |
|------|--------|
| **Model** | MZ-WTR-200 |
| **Target Price** | R649 |
| **Microcontroller** | ESP32-WROOM-32 (or ESP8266 for low-cost variant) |
| **Connectivity** | WiFi (LoRa optional for remote) |
| **Sensors** | HC-SR04 ultrasonic level, DS18B20 temp, flow sensor |
| **Power** | 5V DC (AA battery pack backup) |
| **Battery Life** | 6 months on 3x AA (sleep mode) |
| **Enclosure** | IP65 weatherproof |
| **Mounting** | Top of tank (drilled or adhesive) |

**Firmware Features:**
- Real-time water level (% full, litres remaining)
- Leak detection
- Overflow prevention alert
- Consumption tracking (daily/weekly/monthly)
- Fill time estimation
- Solar pump integration option

### 2.3 Kasi Smart Electricity Monitor

| Item | Detail |
|------|--------|
| **Model** | MZ-ELEC-300 |
| **Target Price** | R749 |
| **Microcontroller** | ESP32-WROOM-32 |
| **Connectivity** | WiFi |
| **Sensors** | PZEM-004T (AC energy), ACS712 (current) |
| **Measurement** | Voltage, Current, Power, Energy, Frequency, PF |
| **Accuracy** | ±1.0% |
| **Power** | Mains-powered (85–265V AC) |
| **Enclosure** | DIN rail mount (DB box compatible) |
| **Safety** | Built-in 10A fuse |

**Firmware Features:**
- Real-time power consumption (kW)
- Daily, weekly, monthly kWh tracking
- Prepaid balance monitoring (manual entry)
- High usage alerts
- Appliance-level tracking (with multiple units)
- Budget management (set daily/monthly limits)
- Data export (CSV)

### 2.4 Smart Rental Room Manager

| Item | Detail |
|------|--------|
| **Model** | MZ-RENT-400 |
| **Target Price** | R1,199 (kit per room) |
| **Microcontroller** | ESP32-WROOM-32 |
| **Connectivity** | WiFi (mesh via ESP-NOW for multi-room) |
| **Sensors** | PZEM-004T (electricity), flow sensor (water), PIR (occupancy) |
| **Display** | Optional OLED screen for tenant |
| **Power** | Mains-powered |
| **Enclosure** | Tamper-proof lockable box |

**Firmware Features:**
- Per-room electricity metering
- Per-room water metering
- Occupancy-based billing
- Monthly usage report for landlord
- Tenant self-service view (via app)
- Deposit management alerts
- Dispute resolution data log

### 2.5 Taxi Fleet Tracker

| Item | Detail |
|------|--------|
| **Model** | MZ-TAXI-500 |
| **Target Price** | R1,499 + R199/month subscription |
| **Microcontroller** | ESP32-WROOM-32 + NEO-6M GPS |
| **Connectivity** | GSM (SIM7000G LTE-M) |
| **Sensors** | GPS, accelerometer, fuel level (optional) |
| **Power** | Vehicle 12/24V with backup battery |
| **Battery Life** | 4 hours on backup |
| **Enclosure** | IP65, hidden-mount design |

**Firmware Features:**
- Real-time GPS tracking (1-minute intervals)
- Geofencing (departure/arrival alerts)
- Driver behaviour monitoring (speeding, harsh braking)
- Fuel consumption tracking (via OBD or manual)
- Vehicle health diagnostics (battery, engine temp)
- Route history playback
- Driver identification (NFC tag)
- Emergency SOS button

---

## 3. Cloud Platform

### 3.1 Architecture

```
[ESP32 Device] → MQTT/TLS → [AWS IoT Core]
                                    ↓
                      [AWS Lambda (Rules Engine)]
                                    ↓
              ┌────────────────────┼────────────────────┐
              ↓                    ↓                    ↓
        [Amazon S3]        [Amazon RDS]          [Amazon SNS]
     (firmware images)   (device data)         (push/SMS alerts)
              ↓                    ↓                    ↓
    [CloudFront CDN]     [AppSync GraphQL]     [Lambda SMS sender]
              ↓                    ↓
        [OTA Updates]     [Mobile App / Web]
```

### 3.2 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/devices` | GET | List user's devices |
| `/api/devices/:id` | GET | Device details and status |
| `/api/devices/:id/data` | GET | Historical sensor data |
| `/api/devices/:id/config` | PUT | Update device configuration |
| `/api/alerts` | GET | List alerts |
| `/api/alerts/:id/ack` | POST | Acknowledge alert |
| `/api/subscriptions` | GET/POST | Manage subscriptions |
| `/api/technicians` | GET | Find nearby technicians (Marketplace) |

### 3.3 Data Storage

| Data Type | Storage | Retention |
|-----------|---------|-----------|
| Sensor readings | RDS (PostgreSQL) | 2 years |
| Device events | RDS (PostgreSQL) | 2 years |
| Firmware images | S3 | Current + 2 previous |
| User data | RDS (PostgreSQL) | Until account deletion |
| Analytics data | Redshift (future) | 5 years |

---

## 4. Mobile App (Mzansi Connect)

### 4.1 Features v1.0 (MVP)

| Feature | Priority | Description |
|---------|----------|-------------|
| Device dashboard | P0 | View all devices and status |
| Real-time data | P0 | Live sensor readings |
| Alert management | P0 | Receive and acknowledge alerts |
| Device configuration | P1 | Settings, alert thresholds |
| Subscription management | P1 | View/change plans, payment |
| User profile | P1 | Account settings, notifications |
| Technician finder | P2 | Locate nearby installers (Marketplace) |
| Data export | P2 | Download CSV reports |

### 4.2 Tech Stack

| Component | Technology |
|-----------|-----------|
| Framework | React Native 0.72+ |
| Navigation | React Navigation |
| State | Redux Toolkit |
| API | Apollo Client (GraphQL) |
| Maps | Mapbox GL |
| Push | Firebase Cloud Messaging |
| Auth | AWS Cognito |
| Payments | PayFast / Yoco SDK |

### 4.3 Supported Platforms

| Platform | Minimum Version | Distribution |
|----------|----------------|-------------|
| Android | 8.0 (API 26) | Google Play Store |
| iOS | 14.0 | Apple App Store |

---

## 5. Security & Compliance

### 5.1 Device Security

| Measure | Implementation |
|---------|---------------|
| Firmware signing | ECDSA signed firmware images |
| Secure boot | ESP32 secure boot enabled |
| Encrypted storage | Flash encryption (AES-256) |
| Secure communication | TLS 1.2/1.3 on all network traffic |
| OTA security | Signed updates only |
| Unique device ID | Hardware-based unique serial |
| Factory reset | Full flash erase option |

### 5.2 Cloud Security

| Measure | Implementation |
|---------|---------------|
| Authentication | AWS Cognito (MFA optional) |
| API security | API Gateway + IAM roles |
| Data encryption | AES-256 at rest, TLS in transit |
| Audit logging | AWS CloudTrail |
| DDoS protection | AWS Shield |
| Access control | Role-based (admin, user, technician) |

### 5.3 Data Privacy (POPIA)

| Requirement | Implementation |
|-------------|---------------|
| Consent | Explicit opt-in during registration |
| Data minimisation | Only essential data collected |
| Access & deletion | User can download or delete data |
| Breach notification | Automated alerting system |
| Data residency | AWS Africa (Cape Town) region |

---

## 6. Testing & Validation

### 6.1 Testing Levels

| Level | Scope | Frequency |
|-------|-------|-----------|
| Unit test | Firmware modules | Per build |
| Integration | Device ↔ Cloud comms | Per build |
| System | Full product function | Per batch |
| Field trial | Real-world conditions | Pilot deployment |
| Regression | After firmware update | Per OTA release |

### 6.2 Environmental Testing

| Test | Standard | Condition |
|------|----------|-----------|
| Temperature | -10°C to +60°C | 8 hours |
| Humidity | 95% RH non-condensing | 8 hours |
| Drop | 1m onto concrete | 6 faces |
| Dust | IP5X | 8 hours |
| Water (external) | IPX4 (splash) | 10 minutes |

---

*This technical specification is a living document and should be updated as products evolve through prototyping and pilot phases.*
