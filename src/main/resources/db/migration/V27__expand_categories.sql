-- -------------------------------------------------------
-- Mở rộng danh mục: thêm VEHICLE, JOB, SERVICE, GOODS
-- Cập nhật sort_order cho các danh mục hiện có
-- -------------------------------------------------------

-- Cập nhật sort_order danh mục hiện có
UPDATE categories SET sort_order = 10 WHERE code = 'REAL_ESTATE_RENT';
UPDATE categories SET sort_order = 20 WHERE code = 'REAL_ESTATE_SALE';

-- Lấy detail_type_id cho CAR
SET @car_detail_type_id = (SELECT id FROM category_detail_types WHERE code = 'CAR');

-- Thêm danh mục cha VEHICLE, CAR_SALE chuyển thành con của VEHICLE
SET @vehicle_id = UUID();
SET @job_id     = UUID();
SET @service_id = UUID();
SET @goods_id   = UUID();

INSERT INTO categories (id, code, name, parent_id, detail_type_id, is_active, sort_order) VALUES
    (@vehicle_id, 'VEHICLE', 'Xe cộ',            NULL, NULL, TRUE,  30),
    (@job_id,     'JOB',     'Việc làm',          NULL, NULL, FALSE, 40),
    (@service_id, 'SERVICE', 'Dịch vụ',           NULL, NULL, FALSE, 50),
    (@goods_id,   'GOODS',   'Mua bán đồ dùng',   NULL, NULL, FALSE, 60);

-- Chuyển CAR_SALE thành con của VEHICLE và cập nhật sort_order
UPDATE categories SET parent_id = @vehicle_id, sort_order = 1 WHERE code = 'CAR_SALE';

-- Thêm danh mục con: Xe cộ
INSERT INTO categories (id, code, name, parent_id, detail_type_id, is_active, sort_order) VALUES
    (UUID(), 'CAR_RENT',       'Thuê ô tô',        @vehicle_id, @car_detail_type_id, TRUE, 2),
    (UUID(), 'MOTORBIKE_SALE', 'Mua bán xe máy',   @vehicle_id, @car_detail_type_id, TRUE, 3),
    (UUID(), 'MOTORBIKE_RENT', 'Thuê xe máy',      @vehicle_id, @car_detail_type_id, TRUE, 4);

-- Thêm danh mục con: Việc làm
INSERT INTO categories (id, code, name, parent_id, detail_type_id, is_active, sort_order) VALUES
    (UUID(), 'JOB_SEEKING', 'Tìm việc',   @job_id, NULL, FALSE, 1),
    (UUID(), 'JOB_OFFER',   'Tuyển dụng', @job_id, NULL, FALSE, 2);

-- Thêm danh mục con: Dịch vụ
INSERT INTO categories (id, code, name, parent_id, detail_type_id, is_active, sort_order) VALUES
    (UUID(), 'SERVICE_TRANSPORT', 'Vận chuyển / Chuyển nhà', @service_id, NULL, FALSE, 1),
    (UUID(), 'SERVICE_REPAIR',    'Sửa chữa / Bảo trì',      @service_id, NULL, FALSE, 2),
    (UUID(), 'SERVICE_TRANSLATE', 'Dịch thuật / Giấy tờ',    @service_id, NULL, FALSE, 3),
    (UUID(), 'SERVICE_OTHER',     'Dịch vụ khác',             @service_id, NULL, FALSE, 4);

-- Thêm danh mục con: Mua bán đồ dùng
INSERT INTO categories (id, code, name, parent_id, detail_type_id, is_active, sort_order) VALUES
    (UUID(), 'GOODS_ELECTRONICS', 'Đồ điện tử',          @goods_id, NULL, FALSE, 1),
    (UUID(), 'GOODS_FASHION',     'Quần áo / Thời trang', @goods_id, NULL, FALSE, 2),
    (UUID(), 'GOODS_HOUSEHOLD',   'Đồ gia dụng',          @goods_id, NULL, FALSE, 3),
    (UUID(), 'GOODS_OTHER',       'Đồ dùng khác',         @goods_id, NULL, FALSE, 4);
