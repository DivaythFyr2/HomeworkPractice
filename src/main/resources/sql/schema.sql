CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS app_user
(
    id                 UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    username           VARCHAR(255) NOT NULL UNIQUE,
    password           VARCHAR(255) NOT NULL,
    role               VARCHAR(50)  NOT NULL,
    account_non_locked BOOLEAN      NOT NULL DEFAULT true,
    failed_attempts    INT          NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_app_user_username ON app_user (username);