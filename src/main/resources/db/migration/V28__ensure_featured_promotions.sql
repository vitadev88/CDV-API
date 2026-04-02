-- V18 bị lỗi trên production, dùng V28 để tạo bảng an toàn
CREATE TABLE IF NOT EXISTS featured_promotions (
    id           CHAR(36)       NOT NULL PRIMARY KEY,
    listing_id   CHAR(36)       NOT NULL,
    package_id   CHAR(36)       NOT NULL,
    user_id      CHAR(36)       NOT NULL,
    start_at     DATETIME(6)    NOT NULL,
    end_at       DATETIME(6)    NOT NULL,
    created_at   DATETIME(6)    NOT NULL,
    updated_at   DATETIME(6),
    CONSTRAINT fk_fpromo_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE,
    CONSTRAINT fk_fpromo_package FOREIGN KEY (package_id) REFERENCES featured_packages(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fpromo_user    FOREIGN KEY (user_id)    REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
