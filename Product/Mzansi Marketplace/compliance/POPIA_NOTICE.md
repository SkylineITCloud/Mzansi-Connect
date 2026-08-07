# POPIA Compliance — Protection of Personal Information Act (South Africa)

*Act 4 of 2013, effective 1 July 2021*

---

## 1. Information Officer

The Information Officer for this project is the repository owner. Contact details are maintained in the repository metadata.

## 2. Personal Information Collected

The Mzansi Marketplace platform collects and processes the following personal information:

| Data | Purpose | Legal Basis |
|---|---|---|
| Technician name, email, phone | Account registration, job matching, payouts | Consent + contractual necessity |
| Technician town/province | Job matching by location | Contractual necessity |
| Technician bio and skills | Public profile for customers | Consent |
| Customer name & location | Job creation, dispatch | Contractual necessity |
| Bank/mobile money details | Payouts (handled by payment provider) | Contractual necessity |
| IP addresses | Security, analytics | Legitimate interest |

## 3. Data Subject Rights (Section 5-18)

Under POPIA, data subjects have the right to:

- **Access** — Request a copy of all personal data held
- **Correction** — Rectify inaccurate or outdated data
- **Deletion** — Request deletion of data (subject to legal retention periods)
- **Objection** — Object to processing for direct marketing or legitimate interest
- **Portability** — Receive data in a structured, machine-readable format

Exercise these rights by contacting the Information Officer.

## 4. Data Retention (Section 14)

- Technician account data: retained for 3 years after account closure (tax/legal requirements)
- Job records: 3 years (tax and dispute resolution)
- Payout records: 5 years (financial record-keeping)
- Reviews: retained while the technician profile is active
- Logs/audit trails: retained for 12 months

## 5. Security Measures (Section 19-22)

Organisational and technical measures implemented:

- **Encryption**: TLS 1.3 for all data in transit; AES-256 for data at rest
- **Access control**: Row-Level Security (RLS) in Supabase; API key authentication
- **Audit logging**: Job and payout lifecycle events are logged
- **Pseudonymisation**: Job listings reference towns, not addresses, until assignment
- **Breach notification**: Notify Information Regulator and affected data subjects within 72 hours (Section 22)
- **Vetting**: Technicians are verified before public listing (Section 11 consent)

## 6. Data Transfers (Section 72)

Data is hosted on South African or EU-based servers (Supabase). No cross-border transfers occur without appropriate safeguards (standard contractual clauses or adequacy determination).

## 7. Third-Party Processors

| Processor | Purpose | Safeguards |
|---|---|---|
| Supabase (supabase.com) | Database, realtime, auth | DPA in place; EU/ZA hosting |
| Payment provider (pending) | Technician payouts | PCI-DSS compliant; DPIA before activation |
| GitHub (git) | Source code hosting | Repository may contain anonymised sample data |

## 8. Consent (Section 11-13)

- Explicit consent is obtained at technician and customer registration
- Public profiles are opt-in (verified technicians only)
- Consent may be withdrawn at any time by deactivating the account
- Processing for legitimate interests is balanced against data subject rights

---

*This notice complies with Section 17 of POPIA. Last updated: July 2026.*
