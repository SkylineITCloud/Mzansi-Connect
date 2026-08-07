# Operational Plan — Mzansi Connect

**A subsidiary of Skyline IT & Cloud**  
**Date:** June 2026

---

## 1. Operations Overview

### 1.1 Operating Model

Mzansi Connect operates as a **light-asset hardware + software company**:
- Product design and firmware development in-house (Durban)
- PCB manufacturing outsourced (PCBWay / JLCPCB)
- Final assembly and testing at Durban workshop
- Cloud infrastructure on AWS (South Africa region)
- Last-mile delivery via Courier Guy / Pudo
- Field support via trained township technicians

### 1.2 Facility Requirements

| Requirement | Detail | Monthly Cost |
|-------------|--------|-------------|
| Workshop space | 30–50m² desk + assembly area | R6,000 |
| Co-located with Skyline IT | Durban office | Included |
| Storage | Racking for 500+ units | R1,000 |

---

## 2. Manufacturing Process

### 2.1 Product Assembly Flow

```
Component Sourcing (weekly)
       ↓
PCB Manufacturing (outsourced, 5–10 day lead)
       ↓
Incoming Inspection (QC check)
       ↓
Firmware Flashing (ESP32 programmer jig)
       ↓
Sensor & Module Assembly (hand-soldering)
       ↓
Enclosure Fitting (3D-printed or injection-moulded)
       ↓
Functional Testing (100% of units)
       ↓
Burn-in Test (24 hours, 100% of first 500 units)
       ↓
Packaging & Labelling
       ↓
Dispatch
```

### 2.2 Production Capacity

| Phase | Units/Month | Staff Required |
|-------|-------------|---------------|
| Pilot (Y1 Q4) | 100–200 | 2 technicians |
| Early (Y1) | 200–400 | 3 technicians |
| Growth (Y2) | 400–1,000 | 5 technicians |
| Scale (Y3) | 1,000–2,500 | 8–10 technicians |

### 2.3 Lead Times

| Stage | Duration |
|-------|----------|
| Component sourcing | 7–14 days |
| PCB manufacturing | 5–10 days |
| Assembly & testing | 3–5 days |
| **Total (order to dispatch)** | **15–29 days** |
| Target from steady-state | 10–14 days |

---

## 3. Quality Assurance

### 3.1 QC Checkpoints

| Checkpoint | Test | Sample |
|-----------|------|--------|
| Incoming components | Visual inspection, meter check | 10% sample |
| After assembly | Power-on, sensor function, connectivity | 100% |
| After burn-in | Full function test | 100% |
| Pre-dispatch | Cosmetic, packaging, accessories | 100% |

### 3.2 Quality Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| Yield rate | >95% | Units passing QC / total assembled |
| Field failure rate | <3% (Year 1) | Warranty claims / units sold |
| Return rate | <2% | Returns / units sold |
| Customer satisfaction | >4.0/5.0 | Post-purchase survey |

---

## 4. Supply Chain

### 4.1 Key Suppliers

| Component | Supplier | Lead Time | Backup |
|-----------|----------|-----------|--------|
| ESP32 modules | Mantech / RS Components | 3–7 days | Communica |
| GSM modules | Alibaba (direct) | 14–21 days | Local distributor |
| Sensors (PIR, door, etc.) | Mantech / DIYElectronics | 3–7 days | RS Components |
| PCBs | PCBWay (China) | 5–10 days | JLCPCB |
| Enclosures | Local 3D print service | 3–5 days | In-house printer |
| Power supplies | RS Components | 3–7 days | Alibaba |

### 4.2 Inventory Management

| Item | Safety Stock | Reorder Point | Max Stock |
|-----|-------------|---------------|-----------|
| ESP32 | 200 units | 100 units | 500 units |
| GSM modules | 100 units | 50 units | 300 units |
| Sensors (each type) | 200 units | 100 units | 500 units |
| PCBs | 300 units | 150 units | 1,000 units |
| Enclosures | 200 units | 100 units | 500 units |

---

## 5. Technology Infrastructure

### 5.1 Cloud Architecture

| Component | Service | Monthly Cost (Y1) |
|-----------|---------|-----------------|
| Compute | AWS EC2 (t3.medium) | R1,500 |
| Database | AWS RDS (PostgreSQL) | R1,200 |
| IoT Backend | AWS IoT Core | R500 |
| Storage | AWS S3 | R200 |
| Messaging | AWS SQS + SNS | R300 |
| Monitoring | AWS CloudWatch | R200 |
| **Total** | | **R3,900** |

### 5.2 Mobile App Stack

| Layer | Technology |
|-------|-----------|
| Framework | React Native + Expo |
| State Management | Redux Toolkit |
| API Layer | REST + WebSockets |
| Push Notifications | Firebase Cloud Messaging |
| Maps | Mapbox (Africa focus) |

### 5.3 Firmware Stack

| Layer | Technology |
|-------|-----------|
| IDE | PlatformIO (VS Code) |
| Framework | ESP-IDF + Arduino |
| Connectivity | WiFi (ESP-NOW), GSM (AT commands), LoRa (RadioLib) |
| OTA Updates | ESP32 HTTP OTA |
| Security | TLS 1.2, AES-128 encryption |

---

## 6. Customer Support Operations

### 6.1 Support Tiers

| Tier | Channel | Response Time | Handles |
|------|---------|--------------|---------|
| L1 — Basic | WhatsApp chatbot + agent | < 30 min | FAQs, troubleshooting guide |
| L2 — Technical | WhatsApp + phone | < 2 hours | Device issues, warranty |
| L3 — Field | On-site technician | < 48 hours | Hardware replacement |

### 6.2 Support Hours

| Period | Hours |
|--------|-------|
| Weekdays | 07:00 – 19:00 SAST |
| Saturdays | 08:00 – 14:00 SAST |
| Emergency (security customers) | 24/7 SMS alert auto-response |

---

## 7. Legal & Compliance

### 7.1 Registrations Required

| Registration | Status | Timeline | Cost |
|-------------|--------|----------|------|
| CIPC company registration | Pending | 1–2 weeks | R3,000 |
| VAT registration | When revenue > R1M | Year 2 | Free |
| ICASA device type approval | Not started | 3–6 months | R7,000 |
| SABS marking (recommended) | Not started | 6–12 months | R15,000+ |
| POPIA compliance | Not started | 1–2 months | Legal fees |

### 7.2 Insurance Requirements

| Insurance Type | Coverage | Annual Cost |
|---------------|----------|-------------|
| Public liability | R5M | R6,000 |
| Product liability | R10M | R8,000 |
| Equipment insurance | R50,000 | R2,000 |
| Business interruption | R200,000 | R2,000 |
| **Total** | | **R18,000** |

---

## 8. Key Operational Risks & Mitigation

| Risk | Probability | Impact | Mitigation |
|------|-----------|--------|-----------|
| Component shortage | Medium | High | Dual sourcing, stock buffer |
| Production quality issues | Low | High | 100% testing, burn-in protocol |
| Cloud outage | Low | Medium | Multi-AZ AWS deployment |
| Staff turnover | Medium | Medium | Cross-training, documentation |
| Theft of inventory | Low | Medium | Secure storage, inventory tracking |
| Load shedding disruption | High | Medium | Solar backup for workshop |

---

*This operational plan should be reviewed monthly during the first year and quarterly thereafter.*
