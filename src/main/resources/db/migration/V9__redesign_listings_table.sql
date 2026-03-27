-- Drop old listings table (no production data yet)
DROP TABLE IF EXISTS listings;

CREATE TABLE listings (
    id              CHAR(36)        NOT NULL,
    category_id     CHAR(36)        NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    description     TEXT,
    price           DECIMAL(15, 2)  NOT NULL,
    price_unit      VARCHAR(20)     NOT NULL DEFAULT 'TOTAL',   -- MONTHLY | TOTAL | NEGOTIABLE
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE | INACTIVE | SOLD | RENTED
    created_by      CHAR(36)        NOT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_listings_category   FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_listings_created_by FOREIGN KEY (created_by)  REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_listings_category   ON listings (category_id);
CREATE INDEX idx_listings_status     ON listings (status);
CREATE INDEX idx_listings_price      ON listings (price);
CREATE INDEX idx_listings_created_at ON listings (created_at);
CREATE INDEX idx_listings_created_by ON listings (created_by);
