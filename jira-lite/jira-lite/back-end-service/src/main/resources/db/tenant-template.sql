CREATE SCHEMA IF NOT EXISTS {schema};

CREATE TABLE IF NOT EXISTS {schema}.users (
                                              id         SERIAL      PRIMARY KEY,
                                              email      TEXT        NOT NULL UNIQUE,
                                              username   TEXT        NOT NULL UNIQUE,
                                              password   TEXT        NOT NULL,
                                              role       VARCHAR(50) NOT NULL,
    tenant_name TEXT        NOT NULL,
    active     BOOLEAN     NOT NULL DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS {schema}.projects (
                                                 id          SERIAL PRIMARY KEY,
                                                 name        TEXT   NOT NULL UNIQUE,
                                                 description TEXT   UNIQUE
);

CREATE TABLE IF NOT EXISTS {schema}.tasks (
                                              id           SERIAL      PRIMARY KEY,
                                              project_id   INTEGER     NOT NULL REFERENCES {schema}.projects(id),
    summary      TEXT        NOT NULL,
    description  TEXT        NOT NULL,
    assignee_id  INTEGER     REFERENCES {schema}.users(id),
    initiator_id INTEGER     REFERENCES {schema}.users(id),
    status       VARCHAR(20) NOT NULL,
    created_at   TIMESTAMP   DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS {schema}.project_users (
                                                      id         SERIAL  PRIMARY KEY,
                                                      user_id    INTEGER NOT NULL REFERENCES {schema}.users(id),
    project_id INTEGER NOT NULL REFERENCES {schema}.projects(id),
    CONSTRAINT uq_project_users_{schema} UNIQUE (user_id, project_id)
    );

CREATE TABLE IF NOT EXISTS {schema}.project_user_roles (
                                                           project_user_id INTEGER     NOT NULL REFERENCES {schema}.project_users(id),
    role            VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS {schema}.comments (
                                                 id          SERIAL  PRIMARY KEY,
                                                 task_id     INTEGER NOT NULL REFERENCES {schema}.tasks(id),
    description TEXT    NOT NULL,
    author_id   INTEGER NOT NULL REFERENCES {schema}.users(id)
    );

CREATE TABLE IF NOT EXISTS {schema}.invitation_token (
                                                         id         SERIAL       PRIMARY KEY,
                                                         email      VARCHAR(255) NOT NULL,
    token      VARCHAR(255) NOT NULL UNIQUE,
    used       BOOLEAN      DEFAULT FALSE,
    expires_at TIMESTAMP    NOT NULL
    );

CREATE TABLE IF NOT EXISTS {schema}.password_reset_tokens (
                                                              id          SERIAL       PRIMARY KEY,
                                                              user_id     INTEGER      NOT NULL UNIQUE REFERENCES {schema}.users(id),
    token       VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,
    created_at  TIMESTAMP    DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_{schema}_tasks_project  ON {schema}.tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_{schema}_tasks_status   ON {schema}.tasks(status);
CREATE INDEX IF NOT EXISTS idx_{schema}_tasks_assignee ON {schema}.tasks(assignee_id);
CREATE INDEX IF NOT EXISTS idx_{schema}_invite_email   ON {schema}.invitation_token(email);
CREATE INDEX IF NOT EXISTS idx_{schema}_users_email    ON {schema}.users(email);