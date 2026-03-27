-- -------------------------------------------------------
-- Seed danh mục ban đầu
-- Dùng biến để tránh lỗi FK khi reference parent_id
-- -------------------------------------------------------

SET @re_rent_id  = UUID();
SET @re_sale_id  = UUID();
SET @car_sale_id = UUID();

-- Danh mục cha
INSERT INTO categories (id, code, name, parent_id, detail_type, sort_order) VALUES
    (@re_rent_id,  'REAL_ESTATE_RENT', 'Cho thuê bất động sản', NULL, 'REAL_ESTATE', 1),
    (@re_sale_id,  'REAL_ESTATE_SALE', 'Mua bán bất động sản',  NULL, 'REAL_ESTATE', 2),
    (@car_sale_id, 'CAR_SALE',         'Mua bán ô tô',          NULL, 'CAR',         3);

-- Danh mục con: Cho thuê BĐS
INSERT INTO categories (id, code, name, parent_id, detail_type, sort_order) VALUES
    (UUID(), 'HOUSE_RENT',     'Cho thuê nhà',   @re_rent_id, 'REAL_ESTATE', 1),
    (UUID(), 'ROOM_RENT',      'Cho thuê phòng', @re_rent_id, 'REAL_ESTATE', 2),
    (UUID(), 'APARTMENT_RENT', 'Cho thuê căn hộ',@re_rent_id, 'REAL_ESTATE', 3);

-- Danh mục con: Mua bán BĐS
INSERT INTO categories (id, code, name, parent_id, detail_type, sort_order) VALUES
    (UUID(), 'HOUSE_SALE',     'Bán nhà',        @re_sale_id, 'REAL_ESTATE', 1),
    (UUID(), 'APARTMENT_SALE', 'Bán căn hộ',     @re_sale_id, 'REAL_ESTATE', 2),
    (UUID(), 'LAND_SALE',      'Bán đất',         @re_sale_id, 'REAL_ESTATE', 3);
