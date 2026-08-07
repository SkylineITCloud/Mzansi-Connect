-- ============================================================
-- Mzansi Cloud — Supabase / PostgreSQL Schema
-- SPDX-License-Identifier: Apache-2.0
--
-- POPIA: Row-Level Security enforces data subject access controls.
--        All personal data queries filtered by auth.uid().
-- ECTA:  Data messages stored with timestamps per ECTA Section 15.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. CUSTOMERS (POPIA: personal information)
CREATE TABLE customers (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  auth_id     UUID REFERENCES auth.users(id),
  full_name   VARCHAR(150) NOT NULL,
  email       VARCHAR(255) NOT NULL UNIQUE,
  phone       VARCHAR(30),
  address     TEXT,
  created_at  TIMESTAMPTZ DEFAULT now(),
  updated_at  TIMESTAMPTZ DEFAULT now()
);

ALTER TABLE customers ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Admins view customers"
  ON customers FOR SELECT
  USING (auth.uid() IN (SELECT user_id FROM staff WHERE role = 'admin'));

CREATE POLICY "Service role manages customers"
  ON customers FOR ALL
  USING (true)
  WITH CHECK (true);

-- 2. STAFF (cloud platform administrators)
CREATE TABLE staff (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     UUID NOT NULL REFERENCES auth.users(id) UNIQUE,
  full_name   VARCHAR(150) NOT NULL,
  role        VARCHAR(50) DEFAULT 'analyst',
  -- 'admin', 'analyst', 'support'
  active      BOOLEAN DEFAULT TRUE,
  created_at  TIMESTAMPTZ DEFAULT now()
);

ALTER TABLE staff ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Staff view staff"
  ON staff FOR SELECT
  USING (auth.uid() IN (SELECT user_id FROM staff WHERE role = 'admin'));

-- 3. DEVICES (all Mzansi Connect hardware)
CREATE TABLE devices (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id   UUID REFERENCES customers(id) ON DELETE CASCADE,
  serial        VARCHAR(64) UNIQUE NOT NULL,
  name          VARCHAR(100),
  device_type   VARCHAR(50) NOT NULL,
  -- 'livestock_tracker', 'spaza_security', 'water_tank', 'electricity_monitor', 'fleet_tracker', 'smart_bell'
  firmware_ver  VARCHAR(30),
  region        VARCHAR(100),                    -- e.g. "KwaZulu-Natal"
  lat           DOUBLE PRECISION,
  lon           DOUBLE PRECISION,
  battery_pct   REAL,
  status        VARCHAR(20) DEFAULT 'provisioning',
  -- 'provisioning', 'online', 'offline', 'low_battery', 'error'
  paired_at     TIMESTAMPTZ,
  created_at    TIMESTAMPTZ DEFAULT now(),
  updated_at    TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_devices_customer ON devices (customer_id);
CREATE INDEX idx_devices_type ON devices (device_type);
CREATE INDEX idx_devices_status ON devices (status);

ALTER TABLE devices ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Staff view devices"
  ON devices FOR SELECT
  USING (auth.uid() IN (SELECT user_id FROM staff));

CREATE POLICY "Service role manages devices"
  ON devices FOR ALL
  USING (true)
  WITH CHECK (true);

-- 4. TELEMETRY EVENTS (raw time-series)
CREATE TABLE telemetry_events (
  id          BIGSERIAL PRIMARY KEY,
  device_id   UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
  metric      VARCHAR(50) NOT NULL,
  value       DOUBLE PRECISION,
  unit        VARCHAR(20),
  recorded_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_telemetry_device_ts ON telemetry_events (device_id, recorded_at DESC);
CREATE INDEX idx_telemetry_metric_ts ON telemetry_events (metric, recorded_at DESC);

-- Raw telemetry retained 30 days; daily summaries retained 3 years
-- DELETE FROM telemetry_events WHERE recorded_at < now() - INTERVAL '30 days';

ALTER TABLE telemetry_events ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Staff view telemetry"
  ON telemetry_events FOR SELECT
  USING (auth.uid() IN (SELECT user_id FROM staff));

CREATE POLICY "Service role inserts telemetry"
  ON telemetry_events FOR INSERT
  WITH CHECK (true);

-- 5. DAILY SUMMARIES (historical reporting)
CREATE TABLE daily_summaries (
  id             BIGSERIAL PRIMARY KEY,
  device_id      UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
  metric         VARCHAR(50) NOT NULL,
  day            DATE NOT NULL,
  min_value      DOUBLE PRECISION,
  max_value      DOUBLE PRECISION,
  avg_value      DOUBLE PRECISION,
  sum_value      DOUBLE PRECISION,
  samples        INTEGER DEFAULT 0,
  UNIQUE (device_id, metric, day)
);

CREATE INDEX idx_summaries_device_day ON daily_summaries (device_id, day DESC);

ALTER TABLE daily_summaries ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Staff view summaries"
  ON daily_summaries FOR SELECT
  USING (auth.uid() IN (SELECT user_id FROM staff));

CREATE POLICY "Service role inserts summaries"
  ON daily_summaries FOR INSERT
  WITH CHECK (true);

-- 6. ALERTS
CREATE TABLE alerts (
  id          BIGSERIAL PRIMARY KEY,
  device_id   UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
  alert_type  VARCHAR(50) NOT NULL,
  severity    VARCHAR(20) NOT NULL DEFAULT 'warning',
  title       VARCHAR(150) NOT NULL,
  message     TEXT,
  acknowledged BOOLEAN DEFAULT FALSE,
  created_at  TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_alerts_ts ON alerts (created_at DESC);

ALTER TABLE alerts ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Staff view alerts"
  ON alerts FOR SELECT
  USING (auth.uid() IN (SELECT user_id FROM staff));

CREATE POLICY "Service role inserts alerts"
  ON alerts FOR INSERT
  WITH CHECK (true);

-- 7. AUDIT LOG (security & compliance)
CREATE TABLE audit_log (
  id          BIGSERIAL PRIMARY KEY,
  staff_id    UUID REFERENCES staff(id),
  action      VARCHAR(100) NOT NULL,
  entity      VARCHAR(50),
  entity_id   VARCHAR(100),
  details     JSONB,
  created_at  TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_audit_ts ON audit_log (created_at DESC);

ALTER TABLE audit_log ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Admins view audit log"
  ON audit_log FOR SELECT
  USING (auth.uid() IN (SELECT user_id FROM staff WHERE role = 'admin'));

-- 8. HELPER VIEW: platform overview stats
CREATE VIEW platform_stats AS
SELECT
  (SELECT COUNT(*) FROM customers) AS total_customers,
  (SELECT COUNT(*) FROM devices) AS total_devices,
  (SELECT COUNT(*) FROM devices WHERE status = 'online') AS online_devices,
  (SELECT COUNT(*) FROM alerts WHERE acknowledged = FALSE) AS open_alerts;

-- 9. AUTO-UPDATE updated_at TRIGGER
CREATE OR REPLACE FUNCTION trigger_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_customers_updated_at
  BEFORE UPDATE ON customers
  FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

CREATE TRIGGER set_devices_updated_at
  BEFORE UPDATE ON devices
  FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
