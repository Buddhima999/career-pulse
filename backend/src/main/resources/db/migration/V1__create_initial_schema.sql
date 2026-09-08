CREATE TABLE users
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL DEFAULT 'USER',
    status        VARCHAR(30)  NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE job_sources
(
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100)  NOT NULL,
    base_url   VARCHAR(2048) NOT NULL,
    active     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_job_sources PRIMARY KEY (id),
    CONSTRAINT uk_job_sources_name UNIQUE (name)
);

CREATE TABLE jobs
(
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    source_id         BIGINT         NOT NULL,
    external_job_id   VARCHAR(255)   NULL,
    title             VARCHAR(255)   NOT NULL,
    company           VARCHAR(255)   NOT NULL,
    location          VARCHAR(255)   NULL,
    description       TEXT           NULL,
    employment_type   VARCHAR(50)    NULL,
    experience_level  VARCHAR(50)    NULL,
    salary_min        DECIMAL(12, 2) NULL,
    salary_max        DECIMAL(12, 2) NULL,
    salary_currency   CHAR(3)        NULL,
    source_url        VARCHAR(2048)  NOT NULL,
    published_at      TIMESTAMP      NULL,
    scraped_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
                                          ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_jobs PRIMARY KEY (id),

    CONSTRAINT uk_jobs_source_external_id
        UNIQUE (source_id, external_job_id),

    CONSTRAINT fk_jobs_source
        FOREIGN KEY (source_id)
        REFERENCES job_sources (id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_jobs_salary_range
        CHECK (
            salary_min IS NULL
            OR salary_max IS NULL
            OR salary_min <= salary_max
        )
);

CREATE TABLE saved_jobs
(
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    user_id            BIGINT      NOT NULL,
    job_id             BIGINT      NOT NULL,
    application_status VARCHAR(30) NOT NULL DEFAULT 'SAVED',
    notes              TEXT        NULL,
    saved_at           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_saved_jobs PRIMARY KEY (id),

    CONSTRAINT uk_saved_jobs_user_job
        UNIQUE (user_id, job_id),

    CONSTRAINT fk_saved_jobs_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_saved_jobs_job
        FOREIGN KEY (job_id)
        REFERENCES jobs (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_saved_jobs_application_status
        CHECK (
            application_status IN (
                'SAVED',
                'APPLIED',
                'INTERVIEW',
                'OFFERED',
                'REJECTED',
                'WITHDRAWN'
            )
        )
);

CREATE TABLE notification_preferences
(
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    user_id       BIGINT      NOT NULL,
    keywords      JSON        NULL,
    locations     JSON        NULL,
    email_enabled BOOLEAN     NOT NULL DEFAULT TRUE,
    frequency     VARCHAR(30) NOT NULL DEFAULT 'DAILY',
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_notification_preferences
        PRIMARY KEY (id),

    CONSTRAINT uk_notification_preferences_user
        UNIQUE (user_id),

    CONSTRAINT fk_notification_preferences_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_notification_preferences_frequency
        CHECK (
            frequency IN (
                'IMMEDIATE',
                'DAILY',
                'WEEKLY'
            )
        )
);

CREATE INDEX idx_jobs_title
    ON jobs (title);

CREATE INDEX idx_jobs_company
    ON jobs (company);

CREATE INDEX idx_jobs_location
    ON jobs (location);

CREATE INDEX idx_jobs_published_at
    ON jobs (published_at);

CREATE INDEX idx_saved_jobs_job_id
    ON saved_jobs (job_id);