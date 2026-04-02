CREATE TABLE featured_packages (
    id           CHAR(36)       NOT NULL PRIMARY KEY,
    name         VARCHAR(100)   NOT NULL,
    price        BIGINT         NOT NULL DEFAULT 0,
    duration_days INT           NOT NULL DEFAULT 7,
    active       TINYINT(1)     NOT NULL DEFAULT 1,
    sort_order   INT            NOT NULL DEFAULT 0,
    created_at   DATETIME(6)    NOT NULL,
    updated_at   DATETIME(6)
);
