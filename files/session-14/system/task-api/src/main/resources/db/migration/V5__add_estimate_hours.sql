-- A schema change after go-live: add a column with a new migration, never by editing V2.
ALTER TABLE tasks ADD COLUMN estimate_hours INTEGER CHECK (estimate_hours BETWEEN 1 AND 100);
UPDATE tasks SET estimate_hours = 2 WHERE estimate_hours IS NULL AND done = FALSE;
