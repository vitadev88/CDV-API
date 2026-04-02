-- -------------------------------------------------------
-- V29: Đảm bảo category_detail_types và detail_type_id đầy đủ
-- -------------------------------------------------------

-- 1. Thêm các loại còn thiếu (INSERT IGNORE tránh duplicate)
INSERT IGNORE INTO category_detail_types (id, code, name, sort_order) VALUES
    (UUID(), 'REAL_ESTATE', 'Bất động sản',      0),
    (UUID(), 'CAR',         'Ô tô / Xe cộ',      1),
    (UUID(), 'JOB',         'Việc làm',           2),
    (UUID(), 'SERVICE',     'Dịch vụ',            3),
    (UUID(), 'GOODS',       'Mua bán đồ dùng',   4);

-- 2. Gán detail_type_id cho danh mục Bất động sản (nếu còn NULL)
UPDATE categories
SET detail_type_id = (SELECT id FROM category_detail_types WHERE code = 'REAL_ESTATE')
WHERE code IN (
    'REAL_ESTATE_RENT', 'REAL_ESTATE_SALE',
    'HOUSE_RENT', 'ROOM_RENT', 'APARTMENT_RENT',
    'HOUSE_SALE', 'APARTMENT_SALE', 'LAND_SALE'
) AND detail_type_id IS NULL;

-- 3. Gán detail_type_id cho danh mục Xe cộ (nếu còn NULL)
UPDATE categories
SET detail_type_id = (SELECT id FROM category_detail_types WHERE code = 'CAR')
WHERE code IN (
    'CAR_SALE', 'CAR_RENT',
    'MOTORBIKE_SALE', 'MOTORBIKE_RENT'
) AND detail_type_id IS NULL;

-- 4. Gán detail_type_id cho danh mục Việc làm (nếu còn NULL)
UPDATE categories
SET detail_type_id = (SELECT id FROM category_detail_types WHERE code = 'JOB')
WHERE code IN ('JOB_SEEKING', 'JOB_OFFER')
AND detail_type_id IS NULL;

-- 5. Gán detail_type_id cho danh mục Dịch vụ (nếu còn NULL)
UPDATE categories
SET detail_type_id = (SELECT id FROM category_detail_types WHERE code = 'SERVICE')
WHERE code IN (
    'SERVICE_TRANSPORT', 'SERVICE_REPAIR',
    'SERVICE_TRANSLATE', 'SERVICE_OTHER'
) AND detail_type_id IS NULL;

-- 6. Gán detail_type_id cho danh mục Đồ dùng (nếu còn NULL)
UPDATE categories
SET detail_type_id = (SELECT id FROM category_detail_types WHERE code = 'GOODS')
WHERE code IN (
    'GOODS_ELECTRONICS', 'GOODS_FASHION',
    'GOODS_HOUSEHOLD', 'GOODS_OTHER'
) AND detail_type_id IS NULL;
