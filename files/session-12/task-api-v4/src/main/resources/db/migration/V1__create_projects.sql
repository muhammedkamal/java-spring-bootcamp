-- Flyway runs every V<n>__*.sql file once, in order, and records it in flyway_schema_history.
-- Never edit a migration that already ran: add a new one.
CREATE TABLE projects (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(80)  NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
