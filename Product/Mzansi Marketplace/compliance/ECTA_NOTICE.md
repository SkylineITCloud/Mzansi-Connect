# ECTA Compliance — Electronic Communications and Transactions Act (South Africa)

*Act 25 of 2002*

---

## 1. Scope

ECTA governs electronic transactions, signatures, and data messages in South Africa. The Mzansi Marketplace platform provides electronic commerce services: job listings, job acceptances, and technician payouts.

## 2. Chapter III — Authentication (Sections 13-20)

- **Electronic signatures**: Technician and customer accounts are authenticated via Supabase Auth (email/password or OAuth). Job acceptance constitutes an electronic transaction compliant with Section 13.
- **Data message validity**: Job assignments, payouts, and consent records are stored as data messages meeting ECTA admissibility requirements (Section 11-15).

## 3. Chapter VII — Consumer Protection (Sections 43-50)

| Section | Requirement | Implementation |
|---|---|---|
| 43(1) | Disclosure of business info | README / about page identifies the supplier |
| 43(2) | Full itemised pricing | Job pay rates disclosed on each listing |
| 43(3) | Cooling-off period | 7-day cancellation right for consumer contracts |
| 44 | Terms and conditions | Available at /terms (or repository README) |
| 45 | Secure payment processing | Payouts via PCI-compliant third-party processor |
| 46-48 | Returns, refunds, warranties | Dispute resolution process documented |

## 4. Chapter VIII — Data Protection (Sections 50-53)

ECTA data protection provisions are superseded by POPIA (Act 4 of 2013) for matters of personal information. See [`POPIA_NOTICE.md`](POPIA_NOTICE.md).

## 5. Section 43(1) — Required Disclosures

The following information is disclosed as required by ECTA:

- **Full name**: [Repository owner / legal entity]
- **Physical address**: [Registered address / contact via repository]
- **Website**: [Repository URL]
- **Email**: [Repository owner contact]
- **Registration number**: [Company/entity reg number, if applicable]
- **Membership of self-regulatory bodies**: [If applicable]
- **Code of conduct**: [Link to code, if applicable]

> *Fields marked [ ] should be completed before commercial deployment in South Africa.*

## 6. Independent Contractor Relationships

- Technicians are independent contractors, not employees
- Each job acceptance creates a separate electronic contract
- Payout terms (amount, timing, method) are disclosed before acceptance
- PAYE/UIF/SDL obligations do not apply to independent contractors; verify with a tax professional

## 7. Liability (Section 73-78)

- **Section 73**: No liability for mere conduit (data transmission)
- **Section 74**: No liability for caching
- **Section 75**: No liability for hosting at the direction of users
- **Section 78**: Take-down notification procedure available for alleged unlawful content

---

*This notice complies with ECTA disclosure requirements.
Consult an attorney for full legal compliance before commercial deployment.
Last updated: July 2026.*
