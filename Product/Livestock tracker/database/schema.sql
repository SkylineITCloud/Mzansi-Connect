-- ============================================================
-- LiveStock GPS Tracker — Supabase / PostgreSQL Schema
-- SPDX-License-Identifier: Apache-2.0
--
-- POPIA: Row-Level Security enforces data subject access controls.
--        All personal data queries filtered by auth.uid().
-- ECTA:  Data messages stored with timestamps per ECTA Section 15.
-- ICASA: Database layer; no radio concerns.
-- ============================================================

-- Enable PostGIS for geospatial queries
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. ANIMALS
CREATE TABLE animals (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tag         VARCHAR(50) UNIQUE NOT NULL,         -- e.g. "AU-EL-0427"
  name        VARCHAR(100),                         -- optional friendly name
  species     VARCHAR(50) DEFAULT 'cattle',         -- cattle, sheep, goat, etc.
  breed       VARCHAR(100),
  color       VARCHAR(50),
  owner_id    UUID REFERENCES auth.users(id),
  active      BOOLEAN DEFAULT TRUE,
  created_at  TIMESTAMPTZ DEFAULT now(),
  updated_at  TIMESTAMPTZ DEFAULT now()
);

ALTER TABLE animals ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own animals"
  ON animals FOR SELECT
  USING (owner_id = auth.uid());

CREATE POLICY "Users can insert their own animals"
  ON animals FOR INSERT
  WITH CHECK (owner_id = auth.uid());

CREATE POLICY "Users can update their own animals"
  ON animals FOR UPDATE
  USING (owner_id = auth.uid());

-- 2. LOCATIONS (time-series GPS pings)
CREATE TABLE locations (
  id          BIGSERIAL PRIMARY KEY,
  animal_id   UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
  point       GEOGRAPHY(POINT, 4326) NOT NULL,
  latitude    DOUBLE PRECISION NOT NULL,
  longitude   DOUBLE PRECISION NOT NULL,
  speed_kmh   REAL DEFAULT 0,
  battery_v   REAL,                                -- collar battery voltage
  recorded_at TIMESTAMPTZ DEFAULT now(),
  received_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_locations_animal_ts
  ON locations (animal_id, recorded_at DESC);

CREATE INDEX idx_locations_geog
  ON locations USING GIST (point);

-- Keep only last 90 days of raw pings (optional — run via pg_cron or a scheduled Edge Function)
-- DELETE FROM locations WHERE recorded_at < now() - INTERVAL '90 days';

ALTER TABLE locations ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view locations of their animals"
  ON locations FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM animals
      WHERE animals.id = locations.animal_id
      AND animals.owner_id = auth.uid()
    )
  );

CREATE POLICY "Service role can insert locations"
  ON locations FOR INSERT
  WITH CHECK (true);  -- restricted by API key on the server side

-- 3. GEOFENCES
CREATE TABLE geofences (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_id    UUID NOT NULL REFERENCES auth.users(id),
  name        VARCHAR(100) NOT NULL,
  boundary    GEOGRAPHY(POLYGON, 4326) NOT NULL,   -- geofence polygon
  active      BOOLEAN DEFAULT TRUE,
  created_at  TIMESTAMPTZ DEFAULT now()
);

ALTER TABLE geofences ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users manage their own geofences"
  ON geofences FOR ALL
  USING (owner_id = auth.uid());

-- 3b. SUB COLLARS (subordinate collars linked to a main collar)
CREATE TABLE sub_collars (
  id            VARCHAR(50) PRIMARY KEY,          -- e.g. "SUB-001"
  main_animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
  tag           VARCHAR(50),                      -- optional display name
  species       VARCHAR(50),
  active        BOOLEAN DEFAULT TRUE,
  created_at    TIMESTAMPTZ DEFAULT now()
);

ALTER TABLE sub_collars ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view sub collars of their animals"
  ON sub_collars FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM animals
      WHERE animals.id = sub_collars.main_animal_id
      AND animals.owner_id = auth.uid()
    )
  );

-- 4. PANIC EVENTS (livestock distress / theft detection)
-- Detected by: speed spike, rapid acceleration, off-hours movement,
--              geofence breach + speed, or external trigger.
-- Sub-collar events: sub_strap_breach, sub_strayed, sub_low_battery,
--                    sub_no_main_contact, sub_forcible_removal
CREATE TABLE panic_events (
  id            BIGSERIAL PRIMARY KEY,
  animal_id     UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
  sub_collar_id VARCHAR(50),                     -- set if triggered by a sub-collar
  trigger       VARCHAR(50) NOT NULL,
  -- Main: 'strap_breach', 'high_speed', 'rapid_accel', 'off_hours_movement',
  --       'geofence_breach_high_speed', 'prolonged_struggle', 'manual_panic', 'no_heartbeat'
  -- Sub:  'sub_strap_breach', 'sub_strayed', 'sub_low_battery',
  --       'sub_no_main_contact', 'sub_forcible_removal'
  severity      VARCHAR(20) NOT NULL DEFAULT 'warning',
  -- 'info', 'warning', 'critical'
  latitude      DOUBLE PRECISION NOT NULL,
  longitude     DOUBLE PRECISION NOT NULL,
  speed_kmh     REAL,
  heading_deg   REAL,
  battery_v     REAL,
  message       TEXT,
  resolved      BOOLEAN DEFAULT FALSE,
  resolved_at   TIMESTAMPTZ,
  created_at    TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_panic_events_animal ON panic_events (animal_id, created_at DESC);
CREATE INDEX idx_panic_events_unresolved ON panic_events (resolved, created_at DESC)
  WHERE resolved = FALSE;

ALTER TABLE panic_events ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view panic events for their animals"
  ON panic_events FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM animals
      WHERE animals.id = panic_events.animal_id
      AND animals.owner_id = auth.uid()
    )
  );

CREATE POLICY "Service role can insert panic events"
  ON panic_events FOR INSERT
  WITH CHECK (true);

-- 5. ALERTS (legacy / geofence-specific)
CREATE TABLE alerts (
  id          BIGSERIAL PRIMARY KEY,
  animal_id   UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
  panic_event_id BIGINT REFERENCES panic_events(id) ON DELETE SET NULL,
  geofence_id UUID REFERENCES geofences(id) ON DELETE SET NULL,
  alert_type  VARCHAR(50) NOT NULL DEFAULT 'geofence_breach',
  -- 'geofence_breach', 'low_battery', 'no_signal', 'panic'
  message     TEXT,
  latitude    DOUBLE PRECISION,
  longitude   DOUBLE PRECISION,
  resolved    BOOLEAN DEFAULT FALSE,
  created_at  TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_alerts_animal ON alerts (animal_id, created_at DESC);

ALTER TABLE alerts ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view alerts for their animals"
  ON alerts FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM animals
      WHERE animals.id = alerts.animal_id
      AND animals.owner_id = auth.uid()
    )
  );

-- 6. HELPER VIEW: unresolved panic events
CREATE VIEW active_panics AS
SELECT
  pe.id,
  pe.animal_id,
  a.tag,
  a.name,
  a.species,
  pe.sub_collar_id,
  pe.trigger,
  pe.severity,
  pe.latitude,
  pe.longitude,
  pe.speed_kmh,
  pe.battery_v,
  pe.message,
  pe.created_at
FROM panic_events pe
JOIN animals a ON a.id = pe.animal_id
WHERE pe.resolved = FALSE
ORDER BY pe.created_at DESC;

-- 7. HELPER VIEW: latest location per animal
CREATE VIEW latest_locations AS
SELECT DISTINCT ON (a.id)
  a.id       AS animal_id,
  a.tag,
  a.name,
  a.species,
  l.latitude,
  l.longitude,
  l.speed_kmh,
  l.battery_v,
  l.recorded_at,
  l.received_at
FROM animals a
LEFT JOIN locations l ON l.animal_id = a.id
ORDER BY a.id, l.recorded_at DESC;

-- 6. AUTO-UPDATE updated_at TRIGGER
CREATE OR REPLACE FUNCTION trigger_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_animals_updated_at
  BEFORE UPDATE ON animals
  FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
