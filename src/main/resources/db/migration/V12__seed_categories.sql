-- -------------------------------------------------------
-- Seed danh mục toàn diện
-- Lưu ý: detail_type dùng chuỗi ở đây, V14 sẽ migrate sang detail_type_id FK
-- is_active = FALSE cho danh mục chưa có handler (JOB, SERVICE, GOODS)
-- -------------------------------------------------------

-- -------------------------------------------------------
-- 1. DANH MỤC CHA
-- -------------------------------------------------------
SET @re_rent_id  = UUID();
SET @re_sale_id  = UUID();
SET @vehicle_id  = UUID();
SET @job_id      = UUID();
SET @service_id  = UUID();
SET @goods_id    = UUID();

INSERT INTO categories (id, code, name, parent_id, detail_type, is_active, sort_order) VALUES
    (@re_rent_id,  'REAL_ESTATE_RENT', 'Cho thuê bất động sản',  NULL, 'REAL_ESTATE', TRUE,  10),
    (@re_sale_id,  'REAL_ESTATE_SALE', 'Mua bán bất động sản',   NULL, 'REAL_ESTATE', TRUE,  20),
    (@vehicle_id,  'VEHICLE',          'Xe cộ',                  NULL, NULL,           TRUE,  30),
    (@job_id,      'JOB',              'Việc làm',               NULL, NULL,           FALSE, 40),
    (@service_id,  'SERVICE',          'Dịch vụ',                NULL, NULL,           FALSE, 50),
    (@goods_id,    'GOODS',            'Mua bán đồ dùng',        NULL, NULL,           FALSE, 60);

-- -------------------------------------------------------
-- 2. CON: CHO THUÊ BẤT ĐỘNG SẢN
-- -------------------------------------------------------
INSERT INTO categories (id, code, name, parent_id, detail_type, is_active, sort_order) VALUES
    (UUID(), 'HOUSE_RENT',     'Cho thuê nhà',    @re_rent_id, 'REAL_ESTATE', TRUE, 1),
    (UUID(), 'ROOM_RENT',      'Cho thuê phòng',  @re_rent_id, 'REAL_ESTATE', TRUE, 2),
    (UUID(), 'APARTMENT_RENT', 'Cho thuê căn hộ', @re_rent_id, 'REAL_ESTATE', TRUE, 3);

-- -------------------------------------------------------
-- 3. CON: MUA BÁN BẤT ĐỘNG SẢN
-- -------------------------------------------------------
INSERT INTO categories (id, code, name, parent_id, detail_type, is_active, sort_order) VALUES
    (UUID(), 'HOUSE_SALE',     'Bán nhà',     @re_sale_id, 'REAL_ESTATE', TRUE, 1),
    (UUID(), 'APARTMENT_SALE', 'Bán căn hộ',  @re_sale_id, 'REAL_ESTATE', TRUE, 2),
    (UUID(), 'LAND_SALE',      'Bán đất',     @re_sale_id, 'REAL_ESTATE', TRUE, 3);

-- ------------------------------------------------------- 
-- 4. CON: XE CỘ (reuse handler CAR)
-- -------------------------------------------------------
INSERT INTO categories (id, code, name, parent_id, detail_type, is_active, sort_order) VALUES
    (UUID(), 'CAR_SALE',       'Mua bán ô tô',          @vehicle_id, 'CAR', TRUE, 1),
    (UUID(), 'CAR_RENT',       'Thuê ô tô',             @vehicle_id, 'CAR', TRUE, 2),
    (UUID(), 'MOTORBIKE_SALE', 'Mua bán xe máy',@vehicle_id, 'CAR', TRUE, 3),
    (UUID(), 'MOTORBIKE_RENT', 'Thuê xe máy',   @vehicle_id, 'CAR', TRUE, 4);

-- -------------------------------------------------------
-- 5. CON: VIỆC LÀM (chưa có handler — is_active = FALSE)
-- -------------------------------------------------------
INSERT INTO categories (id, code, name, parent_id, detail_type, is_active, sort_order) VALUES
    (UUID(), 'JOB_SEEKING', 'Tìm việc',   @job_id, NULL, FALSE, 1),
    (UUID(), 'JOB_OFFER',   'Tuyển dụng', @job_id, NULL, FALSE, 2);

-- -------------------------------------------------------
-- 6. CON: DỊCH VỤ (chưa có handler — is_active = FALSE)
-- -------------------------------------------------------
INSERT INTO categories (id, code, name, parent_id, detail_type, is_active, sort_order) VALUES
    (UUID(), 'SERVICE_TRANSPORT', 'Vận chuyển / Chuyển nhà', @service_id, NULL, FALSE, 1),
    (UUID(), 'SERVICE_REPAIR',    'Sửa chữa / Bảo trì',      @service_id, NULL, FALSE, 2),
    (UUID(), 'SERVICE_TRANSLATE', 'Dịch thuật / Giấy tờ',    @service_id, NULL, FALSE, 3),
    (UUID(), 'SERVICE_OTHER',     'Dịch vụ khác',             @service_id, NULL, FALSE, 4);

-- -------------------------------------------------------
-- 7. CON: MUA BÁN ĐỒ DÙNG (chưa có handler — is_active = FALSE)
-- -------------------------------------------------------
INSERT INTO categories (id, code, name, parent_id, detail_type, is_active, sort_order) VALUES
    (UUID(), 'GOODS_ELECTRONICS', 'Đồ điện tử',          @goods_id, NULL, FALSE, 1),
    (UUID(), 'GOODS_FASHION',     'Quần áo / Thời trang', @goods_id, NULL, FALSE, 2),
    (UUID(), 'GOODS_HOUSEHOLD',   'Đồ gia dụng',          @goods_id, NULL, FALSE, 3),
    (UUID(), 'GOODS_OTHER',       'Đồ dùng khác',         @goods_id, NULL, FALSE, 4);
