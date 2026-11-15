CREATE TABLE tasks (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    priority    VARCHAR(10)  NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    done        BOOLEAN      NOT NULL DEFAULT FALSE,
    due_date    DATE,
    project_id  BIGINT       NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_tasks_title ON tasks (lower(title));
CREATE INDEX ix_tasks_project ON tasks (project_id);
