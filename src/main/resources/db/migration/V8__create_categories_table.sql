CREATE TABLE categories (
    id          CHAR(36)        NOT NULL,
    code        VARCHAR(50)     NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    parent_id   CHAR(36)        NULL,
    detail_type VARCHAR(30)     NULL,   -- REAL_ESTATE | CAR | NULL (for parent-only nodes)
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    sort_order  INT             NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_categories_code   UNIQUE (code),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_categories_parent   ON categories (parent_id);
CREATE INDEX idx_categories_active   ON categories (is_active);
