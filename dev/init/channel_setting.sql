-- PostgreSQL DDL for channel_setting

CREATE TABLE IF NOT EXISTS channel_setting (
  id           BIGSERIAL PRIMARY KEY,
  channel_type VARCHAR(32) NOT NULL,
  bot_name	   VARCHAR(50) NOT NULL,
  settings     TEXT       NOT NULL,
  created_at   TIMESTAMP   NOT NULL DEFAULT now(),
  updated_at   TIMESTAMP   NOT NULL DEFAULT now()
);

-- Optional: keep updated_at in sync at the DB level (if you don't rely on @PreUpdate)
-- Uncomment if you prefer DB-managed timestamps instead of application-managed ones.
-- CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
-- BEGIN
--   NEW.updated_at = now();
--   RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
-- 
-- CREATE TRIGGER trg_set_updated_at
-- BEFORE UPDATE ON channel_setting
-- FOR EACH ROW
-- EXECUTE FUNCTION set_updated_at();
