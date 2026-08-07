# POPIA Compliance — Protection of Personal Information Act (South Africa)

*Act 4 of 2013, effective 1 July 2021*

---

## 1. Information Officer

The Information Officer for this project is the repository owner. Contact details are maintained in the repository metadata.

## 2. Personal Information Collected

The Mzansi Cloud platform stores customer information for device provisioning, billing, and support:

| Data | Purpose | Legal Basis |
|---|---|---|
| User name & email | Account registration, authentication | Consent (Section 11) |
| Phone number | SMS alerts, support | Consent + contractual necessity |
| Physical address | Device installation, dispatch | Contractual necessity |
| Device serials & locations | Service delivery, analytics | Consent + legitimate interest |
| IP addresses | Security, audit logging | Legitimate interest |
| Staff identifiers | Platform administration, audit trail | Legitimate interest |

## 3. Data Subject Rights (Section 5-18)

Under POPIA, data subjects have the right to:

- **Access** — Request a copy of all personal data held
- **Correction** — Rectify inaccurate or outdated data
- **Deletion** — Request deletion of data (subject to legal retention periods)
- **Objection** — Object to processing for direct marketing or legitimate interest
- **Portability** — Receive data in a structured, machine-readable format

Exercise these rights by contacting the Information Officer.

## 4. Data Retention (Section 14)

- Customer account data: retained for 3 years after account closure (tax/legal requirements)
- Raw telemetry: 30 days, then aggregated
- Daily summaries: 3 years for historical reporting
- Audit logs: 5 years (security and compliance)
- Support tickets: 3 years

## 5. Security Measures (Section 19-22)

Organisational and technical measures implemented:

- **Encryption**: TLS 1.3 for all data in transit; AES-256 for data at rest
- **Access control**: Row-Level Security (RLS) in Supabase; admin API key for customer endpoints
- **Role separation**: Admin/analyst/support roles with least-privilege policies
- **Audit logging**: Every staff action on customer data is logged
- **Pseudonymisation**: Analytics aggregate device data; customer tables accessed via admin endpoints only
- **Breach notification**: Notify Information Regulator and affected data subjects within 72 hours (Section 22)

## 6. Data Transfers (Section 72)

Data is hosted on South African or EU-based servers (Supabase). No cross-border transfers occur without appropriate safeguards (standard contractual clauses or adequacy determination).

## 7. Third-Party Processors

| Processor | Purpose | Safeguards |
|---|---|---|
| Supabase (supabase.com) | Database, realtime, auth | DPA in place; EU/ZA hosting |
| GitHub (git) | Source code hosting | Repository may contain anonymised sample data |

## 8. Consent (Section 11-13)

- Explicit consent is obtained at account registration
- Customers are informed of analytics processing in the terms of service
- Consent may be withdrawn at any time by deactivating the account
- Processing for legitimate interests is balanced against data subject rights

---

*This notice complies with Section 17 of POPIA. Last updated: July 2026.*
