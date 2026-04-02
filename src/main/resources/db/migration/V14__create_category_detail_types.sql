-- 1. Create category_detail_types table
CREATE TABLE category_detail_types (
    id         CHAR(36)     NOT NULL,
    code       VARCHAR(50)  NOT NULL,
    name       VARCHAR(100) NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uq_cdt_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Seed existing types
INSERT INTO category_detail_types (id, code, name, sort_order) VALUES
    (UUID(), 'REAL_ESTATE', 'Bất động sản', 0),
    (UUID(), 'CAR', 'Ô tô', 1);

-- 3. Add FK column to categories
ALTER TABLE categories ADD COLUMN detail_type_id CHAR(36) NULL;

-- 4. Migrate existing data
UPDATE categories c
INNER JOIN category_detail_types cdt ON cdt.code = c.detail_type
SET c.detail_type_id = cdt.id
WHERE c.detail_type IS NOT NULL;

-- 5. Add FK constraint
ALTER TABLE categories ADD CONSTRAINT fk_categories_detail_type
    FOREIGN KEY (detail_type_id) REFERENCES category_detail_types(id);

-- 6. Drop old column
ALTER TABLE categories DROP COLUMN detail_type;
