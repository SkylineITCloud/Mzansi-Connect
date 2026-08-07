# Bill of Materials — LiveStock GPS Tracker Collar

## Per-Unit Components

| # | Component | Specification | Qty | Unit Cost | Link / Notes |
|---|---|---|---|---|---|
| 1 | **ESP32 Dev Board** | ESP32-WROOM-32, USB-C, CP2102 | 1 | $4.50 | Use a low-quiescent variant for battery life |
| 2 | **GPS Module** | NEO-6M / NEO-8M, u-blox, 10 Hz | 1 | $6.00 | NEO-8M has better sensitivity; both work |
| 3 | **GSM/GPRS Module** | SIM800L, quad-band, microSIM | 1 | $7.00 | For cellular data; skip if using satellite |
| 4 | **Satellite Modem** (optional) | RockBlock 9603 / Swarm M138 | 1 | $120.00 | Replace SIM800L for true off-grid |
| 5 | **LiPo Charger** | TP4056 with DW01 protection IC | 1 | $1.20 | Built-in overcharge/overdischarge |
| 6 | **LiPo Battery** | 3.7V 5000 mAh, 2-pin JST | 1 | $12.00 | Size: ~70×50×10mm |
| 7 | **Voltage Regulator** | AMS1117-3.3V, SOT-223 | 1 | $0.80 | ESP32 runs at 3.3V |
| 8 | **Solar Panel** | 1W 5V 200mA polycrystalline | 1 | $3.50 | ~80×60mm; trickle-charges TP4056 |
| 9 | **Capacitors** | 100µF electrolytic + 10µF ceramic | 2 | $0.20 | Decoupling on regulator input/output |
| 10 | **LoRa Module** (main + sub) | SX1278 868 MHz, SPI | 1 | $4.00 | Long-range sub-collar communication |
| 11 | **Strap Sensor Wire** | 28 AWG stranded, 1.5m — threaded through strap webbing | 1 | $0.30 | Continuity loop; breaks when strap is cut |
| 12 | **Pull-up Resistor** | 10kΩ, 1/4W, through-hole | 1 | $0.05 | Pulls GPIO HIGH when strap loop is broken |
| 13 | **Enclosure** | IP67 ABS junction box, 120×80×50mm | 1 | $5.00 | Must fit battery + PCB stack |
| 14 | **Nylon Collar** | 50mm wide, adjustable 60–120cm | 1 | $2.50 | Industrial-grade webbing + buckle |
| 15 | **Misc** | Wires, solder, heat shrink, screws | 1 | $1.50 | — |

**Total (cellular version):** **$44.55** (main collar with LoRa + strap sensor)
**Total (sub-collar):** **~$12.00** (ESP32-C3 + LoRa + strap sensor + battery — no GPS/GSM)

**Total (satellite version):** **~$153.20** (with RockBlock)

---

## Bulk Pricing (Estimated)

| Volume | Per-Unit Cost (Cellular) | Per-Unit Cost (Satellite) |
|---|---|---|
| 100 | $32.00 | $135.00 |
| 1 000 | $28.00 | $125.00 |
| 10 000 | $24.00 | $110.00 |

---

## Tools Required for Assembly

- Soldering iron (25–40W) + fine tip
- Multimeter
- Third-hand / helping hands
- Heat gun (for heat shrink)
- USB multimeter (for current draw testing)
- FTDI adapter (for initial ESP32 flashing if no USB port on board)
