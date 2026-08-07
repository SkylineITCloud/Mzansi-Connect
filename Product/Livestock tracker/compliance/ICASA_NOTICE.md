# ICASA Compliance — Radio Frequency Spectrum (South Africa)

*Independent Communications Authority of South Africa Act 13 of 2000*
*Radio Frequency Spectrum Regulations, 2015 (as amended)*

---

## 1. Applicable Regulations

The LiveStock GPS Tracker uses radio frequency modules that fall under ICASA jurisdiction:

| Module | Frequency Band | ICASA Classification | Requirement |
|---|---|---|---|
| ESP32 (WiFi) | 2.4 GHz (ISM) | Licence-exempt (Reg 6) | Type approval per ITU-R SM.329 |
| ESP32 (BLE) | 2.4 GHz | Licence-exempt (Reg 6) | EIRP ≤ 10 mW |
| SIM800L (GSM) | 900 / 1800 MHz | Licensed — network-dependent | Network operator licence covers end devices |
| NEO-6M (GPS) | 1.57542 GHz (L1) | Receive-only | No licence required for reception |

## 2. Compliance Obligations

### 2.1 Type Approval (Regulation 5)

- All radio transmitting modules (ESP32 WiFi, SIM800L GSM) must bear ICASA type-approval marking
- Approved equipment list available at: https://www.icasa.org.za
- The integrator (system builder) is responsible for ensuring approved modules are used

### 2.2 Licence-Exempt Operation (Regulation 6)

The ESP32 operating in the 2.4 GHz ISM band is licence-exempt provided:
- EIRP does not exceed 10 mW/MHz
- Device does not cause harmful interference
- Device accepts any interference received

### 2.3 GSM Operation

SIM800L GSM operation requires:
- A valid SIM card from a licensed South African network operator (Vodacom, MTN, Cell C, Telkom)
- The network operator holds the spectrum licence; the end device operates under that licence
- Device IMEI must be registered on the operator's network

### 2.4 Labelling (Regulation 8)

Each device distributed in South Africa must display:
- ICASA approval mark (if type-approved as a complete product)
- Module type-approval numbers
- Manufacturer name and model number
- "This device complies with ICASA regulations"

## 3. Recommended Labelling Text

> **ICASA COMPLIANCE**
> This device contains ICASA type-approved radio modules.
> WiFi: 2.4 GHz ISM band, EIRP ≤ 100 mW.
> GSM: 900/1800 MHz, network-dependent.
> Complies with ICASA Radio Frequency Spectrum Regulations, 2015.

## 4. Importation (Regulation 17)

Importation of radio devices requires:
- ICASA import permit (if importing completed collars in bulk)
- Supplier's declaration of conformity (DoC)
- Customs clearance with ITAC (International Trade Administration Commission)

## 5. Non-Compliance Penalties

Operating unlicensed or non-approved radio equipment in South Africa may result in:
- Fines under Section 61 of ICASA Act
- Seizure of equipment
- Criminal prosecution in cases of harmful interference

---

*This notice is for informational purposes and does not constitute legal advice.
Consult ICASA or a qualified telecommunications attorney for specific compliance requirements.
Last updated: July 2026.*
