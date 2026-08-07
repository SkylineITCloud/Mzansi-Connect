# POPIA Compliance — Protection of Personal Information Act (South Africa)

*Act 4 of 2013, effective 1 July 2021*

---

## 1. Information Officer

The Information Officer for this project is the repository owner. Contact details are maintained in the repository metadata.

## 2. Personal Information Collected

The LiveStock GPS Tracker system collects and processes the following personal information:

| Data | Purpose | Legal Basis |
|---|---|---|
| User name & email | Account registration, authentication | Consent (Section 11) |
| Farm/enterprise name | Billing, service delivery | Contractual necessity |
| GPS location of livestock | Core service delivery (tracking) | Consent + legitimate interest |
| Device identifiers (MAC, IMEI) | Collar provisioning, support | Contractual necessity |
| IP addresses | Security, analytics | Legitimate interest |

## 3. Data Subject Rights (Section 5–18)

Under POPIA, data subjects have the right to:

- **Access** — Request a copy of all personal data held
- **Correction** — Rectify inaccurate or outdated data
- **Deletion** — Request deletion of data (subject to legal retention periods)
- **Objection** — Object to processing for direct marketing or legitimate interest
- **Portability** — Receive data in a structured, machine-readable format

Exercise these rights by contacting the Information Officer.

## 4. Data Retention (Section 14)

- Active location data: retained for the duration of the service agreement
- Historical location data: anonymised after 90 days for analytics
- Account data: retained for 3 years after account closure (tax/legal requirements)
- Logs/audit trails: retained for 12 months

## 5. Security Measures (Section 19–22)

Organisational and technical measures implemented:

- **Encryption**: TLS 1.3 for all data in transit; AES-256 for data at rest
- **Access control**: Row-Level Security (RLS) in Supabase; API key authentication
- **Audit logging**: All data access is logged with timestamps and user identifiers
- **Pseudonymisation**: Animal IDs are UUIDs; no direct PII in location streams
- **Breach notification**: Notify Information Regulator and affected data subjects within 72 hours (Section 22)

## 6. Data Transfers (Section 72)

Data is hosted on South African or EU-based servers (Supabase). No cross-border transfers occur without appropriate safeguards (standard contractual clauses or adequacy determination).

## 7. Third-Party Processors

| Processor | Purpose | Safeguards |
|---|---|---|
| Supabase (supabase.com) | Database, realtime, auth | DPA in place; EU/ZA hosting |
| Map tiles (OpenStreetMap) | Map rendering | Anonymous requests |
| GitHub (git) | Source code hosting | Repository may contain anonymised sample data |

## 8. Consent (Section 11–13)

- Explicit consent is obtained at account registration
- Collar deployment constitutes consent for location tracking of livestock (not individuals)
- Consent may be withdrawn at any time by deactivating the account
- Processing for legitimate interests is balanced against data subject rights

---

*This notice complies with Section 17 of POPIA. Last updated: July 2026.*
