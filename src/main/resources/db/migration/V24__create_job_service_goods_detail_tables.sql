-- -------------------------------------------------------
-- V24: Detail tables cho JOB, SERVICE, GOODS
-- -------------------------------------------------------

-- 1. listing_job_details
CREATE TABLE listing_job_details (
    listing_id       CHAR(36)       NOT NULL,
    job_type         VARCHAR(20)    NULL,      -- FULL_TIME | PART_TIME | CONTRACT | FREELANCE
    city             VARCHAR(100)   NOT NULL,
    district         VARCHAR(100)   NULL,
    salary_min       DECIMAL(15,2)  NULL,
    salary_max       DECIMAL(15,2)  NULL,
    experience_years INT            NULL,
    contact_name     VARCHAR(100)   NULL,
    PRIMARY KEY (listing_id),
    CONSTRAINT fk_job_details_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_job_details_city ON listing_job_details (city);

-- 2. listing_service_details
CREATE TABLE listing_service_details (
    listing_id    CHAR(36)      NOT NULL,
    city          VARCHAR(100)  NOT NULL,
    district      VARCHAR(100)  NULL,
    contact_name  VARCHAR(100)  NULL,
    contact_phone VARCHAR(20)   NULL,
    PRIMARY KEY (listing_id),
    CONSTRAINT fk_service_details_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. listing_goods_details
CREATE TABLE listing_goods_details (
    listing_id       CHAR(36)      NOT NULL,
    goods_condition  VARCHAR(20)   NULL,     -- NEW | LIKE_NEW | GOOD | FAIR
    brand            VARCHAR(100)  NULL,
    city             VARCHAR(100)  NULL,
    PRIMARY KEY (listing_id),
    CONSTRAINT fk_goods_details_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Seed detail types mới vào category_detail_types
INSERT INTO category_detail_types (id, code, name, sort_order) VALUES
    (UUID(), 'JOB',     'Việc làm',         2),
    (UUID(), 'SERVICE', 'Dịch vụ',           3),
    (UUID(), 'GOODS',   'Mua bán đồ dùng',  4);

-- 5. Gán detail_type_id cho các danh mục lá
UPDATE categories
SET detail_type_id = (SELECT id FROM category_detail_types WHERE code = 'JOB')
WHERE code IN ('JOB_SEEKING', 'JOB_OFFER');

UPDATE categories
SET detail_type_id = (SELECT id FROM category_detail_types WHERE code = 'SERVICE')
WHERE code IN ('SERVICE_TRANSPORT', 'SERVICE_REPAIR', 'SERVICE_TRANSLATE', 'SERVICE_OTHER');

UPDATE categories
SET detail_type_id = (SELECT id FROM category_detail_types WHERE code = 'GOODS')
WHERE code IN ('GOODS_ELECTRONICS', 'GOODS_FASHION', 'GOODS_HOUSEHOLD', 'GOODS_OTHER');
