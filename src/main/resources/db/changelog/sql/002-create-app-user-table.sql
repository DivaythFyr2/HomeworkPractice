CREATE TABLE IF NOT EXISTS app_user
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider         VARCHAR(20)  NOT NULL,
    provider_user_id VARCHAR(128) NOT NULL,
    name             VARCHAR(200) NOT NULL,
    email            VARCHAR(200),
    role             VARCHAR(20)  NOT NULL
);

ALTER TABLE app_user
    ADD CONSTRAINT uq_provider_user UNIQUE (provider, provider_user_id);