CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tasks (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    status       INT NOT NULL DEFAULT 0,
    completed_at TIMESTAMP,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    user_id      BIGINT NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_tasks_user_id ON tasks(user_id);

CREATE TABLE outbox (
    id             UUID PRIMARY KEY,
    event_type     VARCHAR(100) NOT NULL,
    correlation_id VARCHAR(100) NOT NULL,
    payload        JSONB NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    published_at   TIMESTAMP
);

CREATE INDEX idx_outbox_published_at ON outbox(published_at);
