# Feasibility Study — Mzansi Connect

**Prepared for:** Skyline IT & Cloud  
**Date:** June 2026  
**Document Version:** 1.0

---

## Executive Summary

Mzansi Connect is a proposed South African technology subsidiary of Skyline IT & Cloud, focused on developing affordable Internet of Things (IoT) solutions for underserved markets including townships, rural communities, small businesses, and schools. This feasibility study evaluates the technical, financial, operational, and market viability of the venture.

**Key Findings:**
- The South African IoT market is projected to grow at 23.1% CAGR (2025–2030)
- Over 18 million South Africans live in townships with minimal smart technology access
- Low-cost ESP32-based solutions can be developed at 40–60% lower cost than imported alternatives
- Break-even projected within 18–24 months of operations

**Recommendation:** Proceed with development, subject to securing initial seed funding of R350,000–R500,000.

---

## 1. Market Feasibility

### 1.1 Target Market Analysis

| Segment | Size | IoT Adoption | Pain Point |
|---------|------|-------------|------------|
| Township Spaza Shops | ~250,000 | <2% | Theft, stock loss, no security |
| Rural Households (JoJo tanks) | ~3M | <1% | Water waste, no monitoring |
| Township Households | ~7M | <3% | High electricity costs |
| Backyard Tenants/Landlords | ~2M units | <1% | No usage tracking |
| Minibus Taxi Operators | ~150,000 vehicles | <5% | Fuel theft, route inefficiency |

### 1.2 Market Size & Growth

- SA IoT market valued at R4.2 billion (2025)
- Projected to reach R11.8 billion by 2030
- Smart home segment growing at 28% annually
- Commercial IoT (security, fleet) growing at 21% annually

### 1.3 Competitive Landscape

| Competitor | Focus | Price Point | Weakness |
|-----------|-------|-------------|----------|
| IoT.nxt | Enterprise | R50,000+ | Too expensive for SMMEs |
| SqwidNet | LPWAN networks | R200+/month | Connectivity focus only |
| Plentify | Energy monitoring | R3,000+/unit | Narrow product range |
| Global imports (Xiaomi, etc.) | Smart home | R500–R2,000 | Not designed for SA |

**Mzansi Connect Advantage:** 50–70% lower cost, designed for SA conditions, cellular/LoRa support for areas with limited WiFi, local support ecosystem.

---

## 2. Technical Feasibility

### 2.1 Technology Stack

| Component | Technology | Justification |
|-----------|-----------|---------------|
| Microcontroller | ESP32-WROOM | R80–R120 per unit, WiFi + BLE, widely available |
| LoRa Module | SX1278 | R60–R90 per unit, 2–5km range with low power |
| Cellular Module | SIM800L/SIM7000 | R120–R180 per unit, GSM fallback |
| Cloud Backend | AWS IoT Core / Firebase | Scalable, pay-as-you-go, SA region available |
| Mobile App | React Native + Expo | Cross-platform, lower dev cost |
| Firmware | Arduino IDE / PlatformIO | Mature ecosystem, large community |

### 2.2 Connectivity Options

| Option | Range | Cost/Month | Power Use | Best For |
|--------|-------|-----------|-----------|----------|
| WiFi | 50m | R0 (existing) | Medium | Home devices, shops with WiFi |
| LoRaWAN | 2–5km | R15–R25 | Very low | Rural/community networks |
| GSM (2G/4G) | Unlimited | R30–R80 | Medium | Taxi fleet, remote areas |
| Mesh (ESP-NOW) | 200m per hop | R0 | Low | Community mesh internet |

### 2.3 Development Timeline

| Phase | Duration | Key Deliverables |
|-------|----------|-----------------|
| Prototype | 8–12 weeks | 3 working prototypes per product |
| Pilot | 12–16 weeks | 20–50 units deployed in target areas |
| Production | Ongoing | Manufacturing via PCBWay / local assembly |
| App Development | 16–20 weeks | Mzansi Connect App v1.0 |
| Cloud Platform | 12–16 weeks | Dashboard + analytics + marketplace |

### 2.4 Technical Risks & Mitigation

| Risk | Probability | Impact | Mitigation |
|------|-----------|--------|-----------|
| Component shortages | Medium | High | Source from multiple suppliers |
| Network coverage gaps | Medium | Medium | Dual-mode (WiFi + GSM) |
| Device failure rate | Low | Medium | Burn-in testing, warranty system |
| Cybersecurity | Medium | High | End-to-end encryption, OTA updates |

---

## 3. Financial Feasibility

### 3.1 Startup Costs

| Item | Cost (ZAR) |
|------|-----------|
| Prototyping (5 products x 10 units) | R25,000 |
| PCB design & manufacturing setup | R18,000 |
| App development (outsourced MVP) | R80,000 |
| Cloud infrastructure (1st year) | R24,000 |
| Tools & test equipment | R15,000 |
| Legal & company registration | R15,000 |
| Marketing & branding | R25,000 |
| Office/workshop rental (6 months) | R36,000 |
| Miscellaneous & contingency | R37,000 |
| **Total** | **R275,000** |

### 3.2 Unit Cost Breakdown (Smart Spaza Security)

| Component | Cost (ZAR) |
|-----------|-----------|
| ESP32 microcontroller | R95 |
| PIR motion sensor | R25 |
| Magnetic door sensor | R18 |
| GSM module (SIM800L) | R140 |
| Buzzer + LED indicators | R12 |
| Power supply (5V/2A) | R35 |
| Enclosure (3D printed) | R40 |
| PCB | R22 |
| Assembly (labour) | R30 |
| Packaging | R15 |
| **Total BOM** | **R432** |
| Target retail price | **R899** |
| **Gross margin** | **52%** |

### 3.3 Projected Revenue (Year 1–3)

| Year | Units Sold | Avg Price | Revenue | Costs | Net Profit |
|------|-----------|-----------|---------|-------|-----------|
| Year 1 | 1,500 | R799 | R1,198,500 | R978,000 | R220,500 |
| Year 2 | 5,000 | R749 | R3,745,000 | R2,610,000 | R1,135,000 |
| Year 3 | 12,000 | R699 | R8,388,000 | R5,184,000 | R3,204,000 |

### 3.4 Break-Even Analysis

- Monthly fixed costs: R45,000 (rent, salaries, cloud, marketing)
- Gross profit per unit: R367 (avg across product range)
- Break-even volume: 123 units/month
- **Break-even timeline: 14–18 months**

### 3.5 Funding Requirements

| Source | Amount | Terms |
|--------|--------|-------|
| Skyline IT seed funding | R200,000 | Convertible note or equity |
| Small business grant (SEDA/DSBD) | R100,000 | Grant, non-dilutive |
| Bootstrapped revenue | R175,000 | From initial sales |
| **Total** | **R475,000** | |

---

## 4. Operational Feasibility

### 4.1 Team Structure

| Role | Type | Monthly Cost |
|------|------|-------------|
| Founder / CEO (part-time) | Equity + R8,000 | R8,000 |
| IoT Engineer (part-time) | R15,000 | R15,000 |
| Mobile Developer (contractor) | R12,000 | R12,000 |
| Field Technician (x2, commission) | R5,000 base + commission | R10,000 |
| Admin / Support (part-time) | R5,000 | R5,000 |
| **Total monthly payroll** | | **R50,000** |

### 4.2 Key Partners

| Partner | Role |
|---------|------|
| Skyline IT & Cloud | Web infrastructure, parent company support |
| PCBWay / JLCPCB | PCB manufacturing |
| AWS South Africa | Cloud hosting |
| Local electronics distributors | Component sourcing |
| Township SMME associations | Market access & trust |

### 4.3 Regulatory Requirements

| Requirement | Status |
|------------|--------|
| Company registration (CIPC) | Complete (via Skyline IT) |
| VAT registration (R1M+ threshold) | Required in Year 2 |
| ICASA device approval | Required for GSM devices |
| SABS marking | Recommended for liability |
| POPIA compliance | Required for customer data |
| BEE certification | Recommended for government contracts |

---

## 5. Risk Assessment

### 5.1 Key Risks

| Risk | Category | Probability | Impact | Score | Mitigation |
|------|----------|-----------|--------|-------|-----------|
| Low adoption rate | Market | Medium | High | 12 | Pilot program, community ambassadors |
| Component cost inflation | Supply | High | Medium | 12 | Bulk purchasing, alternative suppliers |
| Cash flow shortage | Financial | Medium | High | 12 | Phased rollout, pre-orders |
| Technology failure | Technical | Low | High | 9 | Extensive field testing |
| Competition from imports | Market | Medium | Medium | 9 | SA-first branding, local support |
| Regulatory delays | Legal | Low | Medium | 6 | Early engagement with ICASA |

### 5.2 SWOT Analysis

| Strengths | Weaknesses |
|-----------|-----------|
| SA-specific product design | Limited brand recognition |
| Low-cost manufacturing strategy | Small initial team |
| Parent company support (Skyline IT) | Limited track record |
| Strong community focus | Hardware reliability unknowns |

| Opportunities | Threats |
|-------------|---------|
| Growing IoT adoption in Africa | Cheap Asian imports |
| Government digital inclusion programs | Economic downturn affecting spending |
| Township economic development focus | Component supply chain issues |
| Mobile money & digital payments growth | Load shedding affecting production |

---

## 6. Social Feasibility

### 6.1 Community Impact

- Job creation: 5–15 direct jobs in Year 1, 20–50 by Year 3
- Skills transfer: Training township technicians in IoT installation
- Cost savings: R200–R500/month per household on electricity/water
- Crime reduction: Smart security in spaza shops reduces stock loss by estimated 40–60%

### 6.2 Alignment with National Priorities

| Priority | Alignment |
|----------|----------|
| SA Digital Economy Strategy | Direct — promotes digital inclusion |
| Township Economic Development | Direct — targets township businesses |
| Youth Employment Service | Direct — trains young technicians |
| Water Conservation | Direct — Smart Water Tank Monitor |

---

## 7. Conclusion & Recommendation

### Feasibility Score Summary

| Criteria | Score (1–10) | Weight | Weighted |
|----------|-------------|--------|---------|
| Market Demand | 9 | 25% | 2.25 |
| Technical Viability | 8 | 20% | 1.60 |
| Financial Viability | 7 | 25% | 1.75 |
| Operational Capacity | 7 | 15% | 1.05 |
| Social Impact | 9 | 15% | 1.35 |
| **Total** | | **100%** | **8.00/10** |

### Recommendation

**Proceed with caution.** The study indicates strong market demand and social alignment. Key next steps:

1. Develop 3 working prototypes of the Smart Spaza Security system
2. Deploy a 20-unit pilot in a Durban township
3. Secure R275,000 seed funding from Skyline IT and grants
4. Register with ICASA for GSM device compliance
5. Launch MVP in Q4 2026

---

*Document prepared for internal review. All financial projections are estimates and should be reviewed by a qualified accountant.*
