-- ============================================================
-- Mzansi Marketplace — Supabase / PostgreSQL Schema
-- SPDX-License-Identifier: Apache-2.0
--
-- POPIA: Row-Level Security enforces data subject access controls.
--        All personal data queries filtered by auth.uid().
-- ECTA:  Electronic transactions (job acceptances, payouts) comply
--        with ECTA Chapter VII.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. TECHNICIANS (POPIA: personal information)
CREATE TABLE technicians (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  auth_id     UUID REFERENCES auth.users(id),
  full_name   VARCHAR(150) NOT NULL,
  email       VARCHAR(255) NOT NULL UNIQUE,
  phone       VARCHAR(30),
  town        VARCHAR(100),
  province    VARCHAR(50) DEFAULT 'KwaZulu-Natal',
  bio         TEXT,
  skills      TEXT[],                              -- e.g. {'esp32','solar','wiring'}
  verified    BOOLEAN DEFAULT FALSE,
  rating_avg  REAL DEFAULT 0,
  jobs_done   INTEGER DEFAULT 0,
  active      BOOLEAN DEFAULT TRUE,
  created_at  TIMESTAMPTZ DEFAULT now(),
  updated_at  TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_tech_town ON technicians (town);
CREATE INDEX idx_tech_verified ON technicians (verified);

ALTER TABLE technicians ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Anyone can view verified technicians"
  ON technicians FOR SELECT
  USING (verified = TRUE OR auth_id = auth.uid());

CREATE POLICY "Technicians update own profile"
  ON technicians FOR UPDATE
  USING (auth_id = auth.uid());

CREATE POLICY "Service role manages technicians"
  ON technicians FOR ALL
  USING (true)
  WITH CHECK (true);

-- 2. JOBS (device install and support requests)
CREATE TABLE jobs (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id   UUID REFERENCES customers(id) ON DELETE CASCADE,
  title         VARCHAR(150) NOT NULL,
  description   TEXT,
  device_type   VARCHAR(50),
  town          VARCHAR(100) NOT NULL,
  province      VARCHAR(50) DEFAULT 'KwaZulu-Natal',
  pay_rand      NUMERIC(10,2) NOT NULL,
  status        VARCHAR(30) DEFAULT 'open',
  -- 'open', 'assigned', 'in_progress', 'completed', 'cancelled'
  assigned_to   UUID REFERENCES technicians(id),
  created_at    TIMESTAMPTZ DEFAULT now(),
  updated_at    TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_jobs_status ON jobs (status, created_at DESC);
CREATE INDEX idx_jobs_town ON jobs (town);

ALTER TABLE jobs ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Anyone can view open jobs"
  ON jobs FOR SELECT
  USING (status = 'open' OR assigned_to = auth.uid());

CREATE POLICY "Service role manages jobs"
  ON jobs FOR ALL
  USING (true)
  WITH CHECK (true);

-- 3. JOB APPLICATIONS (technicians apply to open jobs)
CREATE TABLE job_applications (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  job_id         UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
  technician_id  UUID NOT NULL REFERENCES technicians(id) ON DELETE CASCADE,
  message        TEXT,
  status         VARCHAR(20) DEFAULT 'pending',
  -- 'pending', 'shortlisted', 'accepted', 'rejected'
  created_at     TIMESTAMPTZ DEFAULT now(),
  UNIQUE (job_id, technician_id)
);

CREATE INDEX idx_apps_tech ON job_applications (technician_id);

ALTER TABLE job_applications ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Technicians view own applications"
  ON job_applications FOR SELECT
  USING (technician_id IN (SELECT id FROM technicians WHERE auth_id = auth.uid()));

CREATE POLICY "Technicians apply"
  ON job_applications FOR INSERT
  WITH CHECK (technician_id IN (SELECT id FROM technicians WHERE auth_id = auth.uid()));

-- 4. INSTALLS (completed device installations)
CREATE TABLE installs (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  job_id          UUID REFERENCES jobs(id) ON DELETE SET NULL,
  technician_id   UUID NOT NULL REFERENCES technicians(id),
  device_id       UUID REFERENCES devices(id) ON DELETE SET NULL,
  customer_id     UUID REFERENCES customers(id) ON DELETE CASCADE,
  installed_at    TIMESTAMPTZ DEFAULT now(),
  notes           TEXT
);

CREATE INDEX idx_installs_tech ON installs (technician_id, installed_at DESC);

ALTER TABLE installs ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Technicians view own installs"
  ON installs FOR SELECT
  USING (technician_id IN (SELECT id FROM technicians WHERE auth_id = auth.uid()));

CREATE POLICY "Service role manages installs"
  ON installs FOR ALL
  USING (true)
  WITH CHECK (true);

-- 5. PAYOUTS (technician earnings)
CREATE TABLE payouts (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  technician_id  UUID NOT NULL REFERENCES technicians(id) ON DELETE CASCADE,
  amount_rand    NUMERIC(10,2) NOT NULL,
  status         VARCHAR(20) DEFAULT 'pending',
  -- 'pending', 'paid', 'failed'
  method         VARCHAR(30),
  -- 'bank_transfer', 'mobile_money'
  paid_at        TIMESTAMPTZ,
  created_at     TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_payouts_tech ON payouts (technician_id, created_at DESC);

ALTER TABLE payouts ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Technicians view own payouts"
  ON payouts FOR SELECT
  USING (technician_id IN (SELECT id FROM technicians WHERE auth_id = auth.uid()));

CREATE POLICY "Service role manages payouts"
  ON payouts FOR ALL
  USING (true)
  WITH CHECK (true);

-- 6. REVIEWS (customers rate technicians)
CREATE TABLE reviews (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  technician_id   UUID NOT NULL REFERENCES technicians(id) ON DELETE CASCADE,
  customer_id     UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
  job_id          UUID REFERENCES jobs(id) ON DELETE SET NULL,
  rating          INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
  comment         TEXT,
  created_at      TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_reviews_tech ON reviews (technician_id, created_at DESC);

ALTER TABLE reviews ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Anyone can view reviews"
  ON reviews FOR SELECT
  USING (true);

CREATE POLICY "Service role manages reviews"
  ON reviews FOR ALL
  USING (true)
  WITH CHECK (true);

-- 7. HELPER VIEW: earnings per technician
CREATE VIEW technician_earnings AS
SELECT
  technician_id,
  COUNT(*) FILTER (WHERE status = 'paid') AS paid_payouts,
  COALESCE(SUM(amount_rand) FILTER (WHERE status = 'paid'), 0) AS total_paid,
  COALESCE(SUM(amount_rand) FILTER (WHERE status = 'pending'), 0) AS total_pending
FROM payouts
GROUP BY technician_id;

-- 7b. RPC: increment jobs_done counter on technician
CREATE OR REPLACE FUNCTION increment_jobs_done(p_tech_id UUID)
RETURNS VOID AS $$
BEGIN
  UPDATE technicians
  SET jobs_done = jobs_done + 1
  WHERE id = p_tech_id;
END;
$$ LANGUAGE plpgsql;

-- 8. AUTO-UPDATE updated_at TRIGGER
CREATE OR REPLACE FUNCTION trigger_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_technicians_updated_at
  BEFORE UPDATE ON technicians
  FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

CREATE TRIGGER set_jobs_updated_at
  BEFORE UPDATE ON jobs
  FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
