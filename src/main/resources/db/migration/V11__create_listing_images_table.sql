CREATE TABLE listing_images (
    id          CHAR(36)        NOT NULL,
    listing_id  CHAR(36)        NOT NULL,
    url         VARCHAR(500)    NOT NULL,
    is_cover    BOOLEAN         NOT NULL DEFAULT FALSE,
    sort_order  INT             NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_listing_images_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_listing_images_listing ON listing_images (listing_id);
