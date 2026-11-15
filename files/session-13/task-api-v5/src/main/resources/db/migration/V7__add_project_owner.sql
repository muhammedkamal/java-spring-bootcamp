-- Every project now has an owner. Existing projects go to Sara (id 2), then the column becomes NOT NULL.
-- Three steps (add nullable, fill, set NOT NULL) is the safe way to add a required column to a table with data.
ALTER TABLE projects ADD COLUMN owner_id BIGINT REFERENCES users (id);
UPDATE projects SET owner_id = 2;
ALTER TABLE projects ALTER COLUMN owner_id SET NOT NULL;
CREATE INDEX projects_owner_idx ON projects (owner_id);

-- Project names were unique for the whole system. Now they only need to be unique per owner.
ALTER TABLE projects DROP CONSTRAINT projects_name_key;
CREATE UNIQUE INDEX projects_owner_name_unique ON projects (owner_id, lower(name));

INSERT INTO projects (name, description, owner_id) VALUES ('Omar side project', 'Owned by Omar', 3);

-- Same for task titles: unique inside one project, not across everybody's projects.
DROP INDEX ux_tasks_title;
CREATE UNIQUE INDEX ux_tasks_project_title ON tasks (project_id, lower(title));
