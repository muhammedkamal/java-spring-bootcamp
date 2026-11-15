-- Session 12: users who can log in. We store a BCrypt HASH of the password, never the password.
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(120) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    display_name  VARCHAR(80)  NOT NULL,
    role          VARCHAR(10)  NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX users_email_unique ON users (lower(email));

-- Demo accounts. Both passwords are: secret123  (BCrypt, cost 10)
INSERT INTO users (email, password_hash, display_name, role) VALUES
    ('admin@flairs.dev', '$2a$10$9Hz/YZf0rkqQsQGARvKI4ORJW7MVQfGot3ah2sK0clywhYI.W7Sty', 'Admin', 'ADMIN'),
    ('sara@flairs.dev',  '$2a$10$9Hz/YZf0rkqQsQGARvKI4ORJW7MVQfGot3ah2sK0clywhYI.W7Sty', 'Sara',  'USER'),
    ('omar@flairs.dev',  '$2a$10$9Hz/YZf0rkqQsQGARvKI4ORJW7MVQfGot3ah2sK0clywhYI.W7Sty', 'Omar',  'USER');
