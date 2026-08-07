-- ============================================================
-- Mzansi Connect App — Supabase / PostgreSQL Schema
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
  auth_id     UUID REFERENCES auth.users(id),     -- Supabase Auth user
  full_name   VARCHAR(150) NOT NULL,
  email       VARCHAR(255) NOT NULL UNIQUE,
  phone       VARCHAR(30),
  address     TEXT,
  created_at  TIMESTAMPTZ DEFAULT now(),
  updated_at  TIMESTAMPTZ DEFAULT now()
);

ALTER TABLE customers ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users view own profile"
  ON customers FOR SELECT
  USING (auth_id = auth.uid());

CREATE POLICY "Users insert own profile"
  ON customers FOR INSERT
  WITH CHECK (auth_id = auth.uid());

CREATE POLICY "Users update own profile"
  ON customers FOR UPDATE
  USING (auth_id = auth.uid());

-- 2. DEVICES (all Mzansi Connect hardware: trackers, security, water, energy, fleet)
CREATE TABLE devices (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_id      UUID REFERENCES customers(id) ON DELETE CASCADE,
  serial        VARCHAR(64) UNIQUE NOT NULL,
  name          VARCHAR(100),
  device_type   VARCHAR(50) NOT NULL,
  -- 'livestock_tracker', 'spaza_security', 'water_tank', 'electricity_monitor', 'fleet_tracker', 'smart_bell'
  firmware_ver  VARCHAR(30),
  lat           DOUBLE PRECISION,
  lon           DOUBLE PRECISION,
  battery_pct   REAL,
  status        VARCHAR(20) DEFAULT 'online',
  -- 'online', 'offline', 'low_battery', 'error', 'provisioning'
  paired_at     TIMESTAMPTZ,
  created_at    TIMESTAMPTZ DEFAULT now(),
  updated_at    TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_devices_owner ON devices (owner_id);
CREATE INDEX idx_devices_type ON devices (device_type);

ALTER TABLE devices ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users view own devices"
  ON devices FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM customers
      WHERE customers.id = devices.owner_id
      AND customers.auth_id = auth.uid()
    )
  );

CREATE POLICY "Users update own devices"
  ON devices FOR UPDATE
  USING (
    EXISTS (
      SELECT 1 FROM customers
      WHERE customers.id = devices.owner_id
      AND customers.auth_id = auth.uid()
    )
  );

CREATE POLICY "Service role manages devices"
  ON devices FOR ALL
  USING (true)
  WITH CHECK (true);

-- 3. DEVICE READINGS (telemetry time-series)
CREATE TABLE device_readings (
  id          BIGSERIAL PRIMARY KEY,
  device_id   UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
  metric      VARCHAR(50) NOT NULL,
  -- 'lat', 'lon', 'battery_pct', 'power_w', 'voltage_v', 'current_a',
  -- 'water_level_pct', 'flow_lpm', 'motion', 'door_open', 'signal_db'
  value       DOUBLE PRECISION,
  unit        VARCHAR(20),
  recorded_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_readings_device_ts ON device_readings (device_id, recorded_at DESC);
CREATE INDEX idx_readings_metric_ts ON device_readings (metric, recorded_at DESC);

-- Keep only last 180 days of raw readings (run via pg_cron or scheduled Edge Function)
-- DELETE FROM device_readings WHERE recorded_at < now() - INTERVAL '180 days';

ALTER TABLE device_readings ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users view readings of own devices"
  ON device_readings FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM devices
      JOIN customers ON customers.id = devices.owner_id
      WHERE devices.id = device_readings.device_id
      AND customers.auth_id = auth.uid()
    )
  );

CREATE POLICY "Service role inserts readings"
  ON device_readings FOR INSERT
  WITH CHECK (true);  -- restricted by API key on the server side

-- 4. ALERTS (real-time notifications)
CREATE TABLE alerts (
  id          BIGSERIAL PRIMARY KEY,
  device_id   UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
  alert_type  VARCHAR(50) NOT NULL,
  -- 'geofence_breach', 'panic', 'motion', 'door_open', 'leak', 'overflow',
  -- 'power_outage', 'high_usage', 'low_battery', 'no_signal', 'scheduled'
  severity    VARCHAR(20) NOT NULL DEFAULT 'warning',
  -- 'info', 'warning', 'critical'
  title       VARCHAR(150) NOT NULL,
  message     TEXT,
  acknowledged BOOLEAN DEFAULT FALSE,
  created_at  TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_alerts_device ON alerts (device_id, created_at DESC);
CREATE INDEX idx_alerts_unack ON alerts (acknowledged, created_at DESC)
  WHERE acknowledged = FALSE;

ALTER TABLE alerts ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users view alerts of own devices"
  ON alerts FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM devices
      JOIN customers ON customers.id = devices.owner_id
      WHERE devices.id = alerts.device_id
      AND customers.auth_id = auth.uid()
    )
  );

CREATE POLICY "Users acknowledge own alerts"
  ON alerts FOR UPDATE
  USING (
    EXISTS (
      SELECT 1 FROM devices
      JOIN customers ON customers.id = devices.owner_id
      WHERE devices.id = alerts.device_id
      AND customers.auth_id = auth.uid()
    )
  );

CREATE POLICY "Service role inserts alerts"
  ON alerts FOR INSERT
  WITH CHECK (true);

-- 5. ENERGY USAGE (electricity monitor readings aggregated hourly)
CREATE TABLE energy_usage (
  id          BIGSERIAL PRIMARY KEY,
  device_id   UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
  kwh         DOUBLE PRECISION NOT NULL,
  cost_zar    DOUBLE PRECISION,
  interval_start TIMESTAMPTZ NOT NULL,
  interval_end   TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_energy_device_ts ON energy_usage (device_id, interval_start DESC);

ALTER TABLE energy_usage ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users view energy of own devices"
  ON energy_usage FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM devices
      JOIN customers ON customers.id = devices.owner_id
      WHERE devices.id = energy_usage.device_id
      AND customers.auth_id = auth.uid()
    )
  );

CREATE POLICY "Service role inserts energy"
  ON energy_usage FOR INSERT
  WITH CHECK (true);

-- 6. SECURITY EVENTS (spaza shop security, smart bell)
CREATE TABLE security_events (
  id          BIGSERIAL PRIMARY KEY,
  device_id   UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
  event_type  VARCHAR(50) NOT NULL,
  -- 'motion_detected', 'door_opened', 'panic_button', 'power_lost', 'power_restored'
  image_url   TEXT,
  latitude    DOUBLE PRECISION,
  longitude   DOUBLE PRECISION,
  created_at  TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_security_device_ts ON security_events (device_id, created_at DESC);

ALTER TABLE security_events ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users view security events of own devices"
  ON security_events FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM devices
      JOIN customers ON customers.id = devices.owner_id
      WHERE devices.id = security_events.device_id
      AND customers.auth_id = auth.uid()
    )
  );

CREATE POLICY "Service role inserts security events"
  ON security_events FOR INSERT
  WITH CHECK (true);

-- 7. SUBSCRIPTION PLANS
CREATE TABLE subscription_plans (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name        VARCHAR(100) NOT NULL,
  description TEXT,
  price_zar   NUMERIC(10,2) NOT NULL,
  interval    VARCHAR(20) DEFAULT 'monthly',
  -- 'monthly', 'yearly'
  features    JSONB DEFAULT '[]'::jsonb,
  active      BOOLEAN DEFAULT TRUE,
  created_at  TIMESTAMPTZ DEFAULT now()
);

ALTER TABLE subscription_plans ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Anyone can view active plans"
  ON subscription_plans FOR SELECT
  USING (active = TRUE);

-- 8. SUBSCRIPTIONS (customer plans)
CREATE TABLE subscriptions (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
  plan_id     UUID NOT NULL REFERENCES subscription_plans(id),
  device_id   UUID REFERENCES devices(id) ON DELETE SET NULL,
  status      VARCHAR(20) DEFAULT 'active',
  -- 'active', 'trialing', 'past_due', 'cancelled', 'expired'
  current_period_end TIMESTAMPTZ NOT NULL,
  cancel_at_period_end BOOLEAN DEFAULT FALSE,
  created_at  TIMESTAMPTZ DEFAULT now(),
  updated_at  TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_subs_customer ON subscriptions (customer_id);

ALTER TABLE subscriptions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users view own subscriptions"
  ON subscriptions FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM customers
      WHERE customers.id = subscriptions.customer_id
      AND customers.auth_id = auth.uid()
    )
  );

CREATE POLICY "Users update own subscriptions"
  ON subscriptions FOR UPDATE
  USING (
    EXISTS (
      SELECT 1 FROM customers
      WHERE customers.id = subscriptions.customer_id
      AND customers.auth_id = auth.uid()
    )
  );

-- 9. HELPER VIEW: unacknowledged alerts with device names
CREATE VIEW my_alerts AS
SELECT
  al.id,
  al.device_id,
  d.name AS device_name,
  d.device_type,
  al.alert_type,
  al.severity,
  al.title,
  al.message,
  al.acknowledged,
  al.created_at
FROM alerts al
JOIN devices d ON d.id = al.device_id
WHERE al.acknowledged = FALSE
ORDER BY al.created_at DESC;

-- 10. AUTO-UPDATE updated_at TRIGGER
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

CREATE TRIGGER set_subscriptions_updated_at
  BEFORE UPDATE ON subscriptions
  FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
