-- Many-to-many: a task has many tags, a tag is on many tasks. The join table holds the pairs.
CREATE TABLE tags (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE task_tags (
    task_id BIGINT NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    tag_id  BIGINT NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, tag_id)
);
