CREATE TABLE listings (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(255)    NOT NULL,
    description TEXT,
    price       DECIMAL(15, 2)  NOT NULL,
    city        VARCHAR(100)    NOT NULL,
    district    VARCHAR(100),
    type        VARCHAR(20)     NOT NULL,
    created_by  BIGINT          NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_listings_created_by FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_listings_city ON listings (city);
CREATE INDEX idx_listings_price ON listings (price);
CREATE INDEX idx_listings_created_at ON listings (created_at);
CREATE INDEX idx_listings_type ON listings (type);
CREATE INDEX idx_listings_created_by ON listings (created_by);
