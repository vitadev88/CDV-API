CREATE TABLE donation_accounts (
    id          CHAR(36)     NOT NULL PRIMARY KEY,
    bank_name   VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_holder VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    qr_image_url VARCHAR(500),
    active      TINYINT(1)   NOT NULL DEFAULT 1,
    sort_order  INT          NOT NULL DEFAULT 0,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)
);
