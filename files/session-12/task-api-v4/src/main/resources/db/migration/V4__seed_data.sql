INSERT INTO projects (name, description) VALUES
    ('Internship', 'Everything for the Java course'),
    ('Home', 'Personal errands');

INSERT INTO tags (name) VALUES ('spring'), ('java'), ('docker'), ('urgent');

INSERT INTO tasks (title, description, priority, done, due_date, project_id) VALUES
    ('Learn Spring', 'IoC, beans, REST', 'HIGH', TRUE, NULL, 1),
    ('Move the API to PostgreSQL', 'JPA + Flyway', 'HIGH', FALSE, CURRENT_DATE + 2, 1),
    ('Install Docker', NULL, 'MEDIUM', TRUE, NULL, 1),
    ('Read about the N+1 problem', NULL, 'MEDIUM', FALSE, CURRENT_DATE + 5, 1),
    ('Buy groceries', NULL, 'LOW', FALSE, CURRENT_DATE + 1, 2),
    ('Pay the internet bill', NULL, 'HIGH', FALSE, CURRENT_DATE + 3, 2);

INSERT INTO task_tags (task_id, tag_id) VALUES (1, 1), (1, 2), (2, 1), (2, 3), (2, 4), (3, 3), (6, 4);
