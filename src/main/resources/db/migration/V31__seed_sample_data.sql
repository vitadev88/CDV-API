-- =============================================================
-- V31 — Seed sample data (development / demo)
-- Thêm: users, listings (BĐS + xe + việc làm), blog posts đầy đủ
-- =============================================================

-- ─── 0. Biến dùng lại ────────────────────────────────────────────────────────

-- Super Admin user (đã tồn tại, lấy id để reuse)
SET @admin_id = (SELECT id FROM users WHERE role = 'SUPER_ADMIN' LIMIT 1);

-- Lấy category IDs
SET @cat_rent_house    = (SELECT id FROM categories WHERE code = 'HOUSE_RENT'      LIMIT 1);
SET @cat_rent_room     = (SELECT id FROM categories WHERE code = 'ROOM_RENT'       LIMIT 1);
SET @cat_rent_apt      = (SELECT id FROM categories WHERE code = 'APARTMENT_RENT'  LIMIT 1);
SET @cat_sale_house    = (SELECT id FROM categories WHERE code = 'HOUSE_SALE'      LIMIT 1);
SET @cat_sale_apt      = (SELECT id FROM categories WHERE code = 'APARTMENT_SALE'  LIMIT 1);
SET @cat_sale_land     = (SELECT id FROM categories WHERE code = 'LAND_SALE'       LIMIT 1);
SET @cat_car_sale      = (SELECT id FROM categories WHERE code = 'CAR_SALE'        LIMIT 1);
SET @cat_car_rent      = (SELECT id FROM categories WHERE code = 'CAR_RENT'        LIMIT 1);
SET @cat_moto_sale     = (SELECT id FROM categories WHERE code = 'MOTORBIKE_SALE'  LIMIT 1);
SET @cat_moto_rent     = (SELECT id FROM categories WHERE code = 'MOTORBIKE_RENT'  LIMIT 1);

-- Blog category IDs
SET @blog_cat_exp      = (SELECT id FROM blog_categories WHERE slug = 'kinh-nghiem-song' LIMIT 1);
SET @blog_cat_job      = (SELECT id FROM blog_categories WHERE slug = 'viec-lam'         LIMIT 1);
SET @blog_cat_food     = (SELECT id FROM blog_categories WHERE slug = 'am-thuc'          LIMIT 1);
SET @blog_cat_travel   = (SELECT id FROM blog_categories WHERE slug = 'du-lich'          LIMIT 1);
SET @blog_cat_news     = (SELECT id FROM blog_categories WHERE slug = 'tin-tuc'          LIMIT 1);

-- ─── 1. Thêm blog categories nếu chưa có ────────────────────────────────────

INSERT IGNORE INTO blog_categories (id, name, slug, description, created_at, updated_at)
VALUES
    ('a1b2c3d4-0001-0001-0001-000000000001', 'Kinh nghiệm sống',  'kinh-nghiem-song', 'Chia sẻ kinh nghiệm sống tại Đài Loan', NOW(6), NOW(6)),
    ('a1b2c3d4-0001-0001-0001-000000000002', 'Việc làm',          'viec-lam',         'Tìm việc và kinh nghiệm làm việc',       NOW(6), NOW(6)),
    ('a1b2c3d4-0001-0001-0001-000000000003', 'Ẩm thực',           'am-thuc',          'Ẩm thực Việt Nam và Đài Loan',           NOW(6), NOW(6)),
    ('a1b2c3d4-0001-0001-0001-000000000004', 'Du lịch',           'du-lich',          'Điểm du lịch và trải nghiệm',            NOW(6), NOW(6));

-- Cập nhật lại biến sau khi insert
SET @blog_cat_exp    = 'a1b2c3d4-0001-0001-0001-000000000001';
SET @blog_cat_job    = 'a1b2c3d4-0001-0001-0001-000000000002';
SET @blog_cat_food   = 'a1b2c3d4-0001-0001-0001-000000000003';
SET @blog_cat_travel = 'a1b2c3d4-0001-0001-0001-000000000004';

-- ─── 2. Seed demo users ──────────────────────────────────────────────────────
-- Password BCrypt cho "Demo@123456" (tất cả users dùng cùng password)

INSERT IGNORE INTO users (id, email, phone, username, password, full_name, role, verified, created_at, updated_at)
VALUES
    ('a0000001-0000-0000-0000-000000000001', 'nguyen.van.a@gmail.com',  '0901111001', 'nguyenvana',  '$2a$10$N.zmdr9zkoa05NDLZoIs7O5G9QoUxUqoMDrQSSSvjWhN7N62bEtv2', 'Nguyễn Văn A',  'USER', TRUE, NOW(), NOW()),
    ('a0000001-0000-0000-0000-000000000002', 'tran.thi.b@gmail.com',    '0901111002', 'tranthib',    '$2a$10$N.zmdr9zkoa05NDLZoIs7O5G9QoUxUqoMDrQSSSvjWhN7N62bEtv2', 'Trần Thị B',    'USER', TRUE, NOW(), NOW()),
    ('a0000001-0000-0000-0000-000000000003', 'le.van.c@gmail.com',      '0901111003', 'levanc',      '$2a$10$N.zmdr9zkoa05NDLZoIs7O5G9QoUxUqoMDrQSSSvjWhN7N62bEtv2', 'Lê Văn C',      'USER', TRUE, NOW(), NOW()),
    ('a0000001-0000-0000-0000-000000000004', 'pham.thi.d@gmail.com',    '0901111004', 'phamthid',    '$2a$10$N.zmdr9zkoa05NDLZoIs7O5G9QoUxUqoMDrQSSSvjWhN7N62bEtv2', 'Phạm Thị D',    'USER', TRUE, NOW(), NOW()),
    ('a0000001-0000-0000-0000-000000000005', 'hoang.van.e@gmail.com',   '0901111005', 'hoangvane',   '$2a$10$N.zmdr9zkoa05NDLZoIs7O5G9QoUxUqoMDrQSSSvjWhN7N62bEtv2', 'Hoàng Văn E',   'USER', TRUE, NOW(), NOW());

-- ─── 3. Blog posts đầy đủ (15 bài) ──────────────────────────────────────────

INSERT IGNORE INTO blog_posts (id, title, slug, content, excerpt, thumbnail, category_id, author_id, tags, status, view_count, published_at, created_at, updated_at)
VALUES
-- === Kinh nghiệm sống ===
(
    'b0000003-0000-0000-0000-000000000003',
    'Cách làm thẻ ARC và những điều cần biết',
    'cach-lam-the-arc-va-nhung-dieu-can-biet',
    '<h2>Thẻ ARC là gì?</h2><p>Thẻ ARC (Alien Resident Certificate) hay còn gọi là thẻ cư trú, là giấy tờ quan trọng nhất đối với người nước ngoài sinh sống tại Đài Loan. Thẻ này cho phép bạn mở tài khoản ngân hàng, ký hợp đồng thuê nhà, đăng ký dịch vụ điện thoại và nhiều dịch vụ khác.</p><h2>Điều kiện để làm thẻ ARC</h2><ul><li>Có visa cư trú hợp lệ (không phải visa du lịch)</li><li>Hợp đồng lao động với công ty tại Đài Loan (đối với lao động)</li><li>Giấy nhập học (đối với du học sinh)</li></ul><h2>Hồ sơ cần chuẩn bị</h2><ol><li>Hộ chiếu còn hạn ít nhất 6 tháng</li><li>Ảnh 2x2 inch (nền trắng)</li><li>Phí làm thẻ (khoảng 1000 NT$)</li><li>Giấy chứng minh cư trú</li></ol>',
    'Hướng dẫn chi tiết cách đăng ký thẻ cư trú ARC tại Đài Loan cho lao động và du học sinh.',
    'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&q=80',
    @blog_cat_exp,
    @admin_id,
    '["arc", "giấy tờ", "cư trú", "đài loan"]',
    'PUBLISHED', 5625,
    DATE_SUB(NOW(), INTERVAL 10 DAY),
    DATE_SUB(NOW(), INTERVAL 10 DAY),
    NOW()
),
(
    'b0000003-0000-0000-0000-000000000009',
    'Kinh nghiệm thuê nhà tại Đài Bắc lần đầu',
    'kinh-nghiem-thue-nha-tai-dai-bac-lan-dau',
    '<h2>Bắt đầu từ đâu?</h2><p>Tìm nhà tại Đài Bắc không quá khó nếu bạn biết đúng kênh. Các trang web như 591.com.tw là lựa chọn phổ biến nhất. Ngoài ra, nhiều nhóm Facebook của cộng đồng người Việt cũng thường xuyên có thông tin cho thuê.</p><h2>Lưu ý quan trọng khi xem nhà</h2><ul><li>Kiểm tra kỹ điện, nước, gas</li><li>Hỏi rõ phí quản lý hàng tháng</li><li>Đọc kỹ hợp đồng trước khi ký</li><li>Chụp ảnh toàn bộ căn phòng trước khi nhận</li></ul>',
    'Những điều cần biết khi lần đầu thuê nhà tại Đài Bắc: từ tìm nhà, xem nhà đến ký hợp đồng.',
    'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800&q=80',
    @blog_cat_exp,
    'a0000001-0000-0000-0000-000000000001',
    '["thuê nhà", "đài bắc", "kinh nghiệm", "nơi ở"]',
    'PUBLISHED', 1245,
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    NOW()
),
(
    'b0000003-0000-0000-0000-000000000010',
    'Mở tài khoản ngân hàng tại Đài Loan: Hướng dẫn từng bước',
    'mo-tai-khoan-ngan-hang-tai-dai-loan-huong-dan-tung-buoc',
    '<h2>Ngân hàng nào phù hợp?</h2><p>Đối với người mới đến Đài Loan, ngân hàng E.SUN (玉山銀行) và Cathay United Bank (國泰世華銀行) được nhiều người Việt lựa chọn nhất vì có giao diện app tiếng Anh và hỗ trợ chuyển tiền quốc tế tốt.</p><h2>Giấy tờ cần thiết</h2><ol><li>Thẻ ARC còn hạn</li><li>Hộ chiếu</li><li>Số điện thoại Đài Loan</li><li>Tiền mặt (để nộp số dư tối thiểu)</li></ol>',
    'Hướng dẫn mở tài khoản ngân hàng tại Đài Loan: cần giấy tờ gì, ngân hàng nào tốt nhất cho người Việt.',
    'https://images.unsplash.com/photo-1601597111158-2fceff292cdc?w=800&q=80',
    @blog_cat_exp,
    'a0000001-0000-0000-0000-000000000002',
    '["ngân hàng", "tài khoản", "tài chính", "đài loan"]',
    'PUBLISHED', 3890,
    DATE_SUB(NOW(), INTERVAL 7 DAY),
    DATE_SUB(NOW(), INTERVAL 7 DAY),
    NOW()
),
(
    'b0000003-0000-0000-0000-000000000011',
    'Bảo hiểm y tế NHI tại Đài Loan: Cách đăng ký và sử dụng',
    'bao-hiem-y-te-nhi-tai-dai-loan-cach-dang-ky-va-su-dung',
    '<h2>NHI là gì?</h2><p>Bảo hiểm y tế quốc gia NHI (National Health Insurance) là một trong những điểm cộng lớn nhất khi sống tại Đài Loan. Chi phí khám chữa bệnh cực kỳ thấp so với không có bảo hiểm.</p><h2>Ai được tham gia?</h2><p>Người có thẻ ARC và đã cư trú tại Đài Loan liên tục từ 6 tháng trở lên. Tuy nhiên lao động theo diện hợp đồng được tham gia ngay từ ngày đầu tiên.</p>',
    'Tất cả những gì bạn cần biết về bảo hiểm y tế NHI tại Đài Loan: ai được tham gia, phí bảo hiểm và cách sử dụng.',
    'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=800&q=80',
    @blog_cat_exp,
    'a0000001-0000-0000-0000-000000000003',
    '["bảo hiểm", "y tế", "NHI", "sức khỏe"]',
    'PUBLISHED', 4120,
    DATE_SUB(NOW(), INTERVAL 15 DAY),
    DATE_SUB(NOW(), INTERVAL 15 DAY),
    NOW()
),
-- === Việc làm ===
(
    'b0000003-0000-0000-0000-000000000004',
    'Tìm việc làm tại Đài Loan - Kinh nghiệm từ A đến Z',
    'tim-viec-lam-tai-dai-loan-kinh-nghiem-tu-a-den-z',
    '<h2>Kênh tìm việc hiệu quả</h2><p>Đài Loan có nhiều kênh tìm việc đáng tin cậy. 104.com.tw là trang tuyển dụng lớn nhất, nhưng với người Việt mới sang, JobsDB và các group Facebook cộng đồng Việt thường dễ tiếp cận hơn.</p><h2>Chuẩn bị CV như thế nào?</h2><p>CV tại Đài Loan thường ngắn gọn hơn CV Việt Nam, tập trung vào kinh nghiệm và kỹ năng cụ thể. Bạn nên có cả bản tiếng Anh và tiếng Trung (nếu có thể).</p>',
    'Tổng hợp kinh nghiệm tìm việc làm tại Đài Loan cho người Việt từ cách tìm việc đến phỏng vấn.',
    'https://images.unsplash.com/photo-1521737711867-e3b97375f902?w=800&q=80',
    @blog_cat_job,
    @admin_id,
    '["việc làm", "tuyển dụng", "đài loan", "cv"]',
    'PUBLISHED', 4325,
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    NOW()
),
(
    'b0000003-0000-0000-0000-000000000012',
    'Học tiếng Trung tại Đài Loan - Nên học ở đâu?',
    'hoc-tieng-trung-tai-dai-loan-nen-hoc-o-dau',
    '<h2>Vì sao phải học tiếng Trung?</h2><p>Dù bạn làm trong môi trường quốc tế, tiếng Trung vẫn là lợi thế cực lớn khi sống tại Đài Loan. Bạn có thể giao tiếp với hàng xóm, đi chợ, đặt đồ ăn và xử lý các tình huống hàng ngày dễ dàng hơn nhiều.</p><h2>Các trung tâm tiếng Trung uy tín</h2><ul><li>MLC (Mandarin Learning Center) - NTU: Nổi tiếng nhất, nhiều khóa học linh hoạt</li><li>NTNU MTC: Chất lượng cao, nhiều sinh viên quốc tế</li><li>Các ứng dụng: HelloChinese, Duolingo (bổ sung)</li></ul>',
    'Tổng hợp các trung tâm tiếng Trung uy tín tại Đài Loan và kinh nghiệm tự học hiệu quả.',
    'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=800&q=80',
    @blog_cat_job,
    'a0000001-0000-0000-0000-000000000004',
    '["tiếng trung", "học tiếng", "đài loan", "ngôn ngữ"]',
    'PUBLISHED', 2787,
    DATE_SUB(NOW(), INTERVAL 20 DAY),
    DATE_SUB(NOW(), INTERVAL 20 DAY),
    NOW()
),
(
    'b0000003-0000-0000-0000-000000000013',
    'Những nghề nghiệp phổ biến nhất của người Việt tại Đài Loan',
    'nhung-nghe-nghiep-pho-bien-nhat-cua-nguoi-viet-tai-dai-loan',
    '<h2>Ngành sản xuất (製造業)</h2><p>Đây là ngành thu hút đông đảo lao động Việt Nam nhất. Các nhà máy điện tử, may mặc, thực phẩm tuyển dụng số lượng lớn với mức lương ổn định.</p><h2>Ngành chăm sóc sức khỏe</h2><p>Người Việt làm nghề hộ lý (看護) ngày càng nhiều. Công việc vất vả nhưng mức lương khá cao, thường được bao ăn ở.</p><h2>F&B và Nhà hàng Việt</h2><p>Nhà hàng và quán ăn Việt Nam mọc lên nhanh chóng tại Đài Loan, tạo ra nhiều cơ hội việc làm trong ngành ẩm thực.</p>',
    'Khảo sát các ngành nghề phổ biến nhất của cộng đồng người Việt tại Đài Loan và mức thu nhập tương ứng.',
    'https://images.unsplash.com/photo-1542744173-8e7e53415bb0?w=800&q=80',
    @blog_cat_job,
    'a0000001-0000-0000-0000-000000000005',
    '["việc làm", "nghề nghiệp", "thu nhập", "lao động"]',
    'PUBLISHED', 3210,
    DATE_SUB(NOW(), INTERVAL 25 DAY),
    DATE_SUB(NOW(), INTERVAL 25 DAY),
    NOW()
),
-- === Ẩm thực ===
(
    'b0000003-0000-0000-0000-000000000005',
    'Top 10 quán ăn Việt Nam ngon nhất tại Đài Bắc',
    'top-10-quan-an-viet-nam-ngon-nhat-tai-dai-bac',
    '<h2>1. Phở Hà Nội - Zhongshan</h2><p>Quán phở truyền thống của chị Lan người Hà Nội, nổi tiếng với nước dùng trong và ngọt. Địa chỉ: Số 12, Đường Zhongshan N, Taipei.</p><h2>2. Bún bò Huế - Da''an</h2><p>Bún bò chuẩn vị miền Trung, sợi bún to, nước dùng đậm đà với mắm ruốc và sả.</p><h2>3. Bánh mì Sài Gòn - Tamsui</h2><p>Bánh mì giòn, nhân đầy đặn theo phong cách Sài Gòn. Rất được người Đài Loan yêu thích.</p>',
    'Danh sách 10 quán ăn Việt Nam được người Việt xa xứ tại Đài Bắc yêu thích nhất.',
    'https://images.unsplash.com/photo-1555126634-323283e090fa?w=800&q=80',
    @blog_cat_food,
    @admin_id,
    '["ẩm thực", "quán ăn", "đài bắc", "việt nam", "phở"]',
    'PUBLISHED', 3854,
    DATE_SUB(NOW(), INTERVAL 3 DAY),
    DATE_SUB(NOW(), INTERVAL 3 DAY),
    NOW()
),
(
    'b0000003-0000-0000-0000-000000000014',
    'Chợ đêm Đài Loan: Thiên đường ẩm thực không thể bỏ qua',
    'cho-dem-dai-loan-thien-duong-am-thuc-khong-the-bo-qua',
    '<h2>Chợ đêm Shilin - Lớn nhất Đài Bắc</h2><p>Chợ đêm Shilin (Sĩ Lâm) là điểm đến ẩm thực không thể bỏ qua khi đến Đài Bắc. Nổi tiếng với các món: gà chiên lớn (大雞排), oyster omelet (蚵仔煎), bubble tea và nhiều món ăn đường phố hấp dẫn khác.</p><h2>Chợ đêm Ningxia</h2><p>Nhỏ hơn Shilin nhưng được nhiều người Đài Loan yêu thích hơn vì ít khách du lịch và giá cả thực tế hơn.</p>',
    'Khám phá những chợ đêm nổi tiếng nhất Đài Loan: từ Shilin, Ningxia đến Raohe và những món ăn đặc sắc.',
    'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&q=80',
    @blog_cat_food,
    'a0000001-0000-0000-0000-000000000001',
    '["chợ đêm", "ẩm thực", "street food", "đài loan"]',
    'PUBLISHED', 2890,
    DATE_SUB(NOW(), INTERVAL 8 DAY),
    DATE_SUB(NOW(), INTERVAL 8 DAY),
    NOW()
),
-- === Du lịch ===
(
    'b0000003-0000-0000-0000-000000000006',
    'Du lịch Jiufen - Làng cổ đẹp như tranh vẽ',
    'du-lich-jiufen-lang-co-dep-nhu-tranh-ve',
    '<h2>Jiufen - Nguồn cảm hứng của Spirited Away</h2><p>Jiufen (九份) là ngôi làng cổ nằm trên núi ở huyện Tân Bắc, cách Đài Bắc khoảng 1 tiếng đi xe buýt. Nơi đây được cho là nguồn cảm hứng để Hayao Miyazaki tạo nên bộ phim hoạt hình nổi tiếng Spirited Away.</p><h2>Cách đến Jiufen</h2><ol><li>Từ ga Taipei Main Station, đi tàu hỏa đến ga Ruifang</li><li>Từ ga Ruifang, đi xe buýt 788 lên Jiufen (khoảng 20 phút)</li></ol><h2>Ăn gì tại Jiufen</h2><ul><li>Bánh gạo hình cá chép (鯉魚旗)</li><li>Bánh khoai môn (芋圓)</li><li>Chả cá viên (魚丸)</li></ul>',
    'Khám phá ngôi làng cổ Jiufen huyền ảo - điểm du lịch không thể bỏ qua khi đến Đài Loan.',
    'https://images.unsplash.com/photo-1470004914212-05527e49370b?w=800&q=80',
    @blog_cat_travel,
    'a0000001-0000-0000-0000-000000000002',
    '["du lịch", "jiufen", "đài loan", "làng cổ"]',
    'PUBLISHED', 2340,
    DATE_SUB(NOW(), INTERVAL 29 DAY),
    DATE_SUB(NOW(), INTERVAL 29 DAY),
    NOW()
),
(
    'b0000003-0000-0000-0000-000000000015',
    'Hành trình Sun Moon Lake - Hồ Nhật Nguyệt thơ mộng',
    'hanh-trinh-sun-moon-lake-ho-nhat-nguyet-tho-mong',
    '<h2>Sun Moon Lake là gì?</h2><p>Sun Moon Lake (日月潭 - Hồ Nhật Nguyệt) là hồ nước ngọt lớn nhất Đài Loan, nằm ở huyện Nam Đầu (Nantou). Hồ có hình dạng nửa tròn như mặt trời và nửa giống mặt trăng lưỡi liềm, đây là lý do cho cái tên đặc biệt của nó.</p><h2>Di chuyển</h2><p>Từ Đài Bắc đến Sun Moon Lake mất khoảng 3 giờ. Bạn có thể đi xe buýt từ bến xe Taipei West khu A.</p>',
    'Hành trình khám phá hồ Nhật Nguyệt Sun Moon Lake: cách đi, ở đâu, ăn gì và những điểm check-in đẹp nhất.',
    'https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=800&q=80',
    @blog_cat_travel,
    'a0000001-0000-0000-0000-000000000003',
    '["du lịch", "sun moon lake", "hồ nhật nguyệt", "nam đầu"]',
    'PUBLISHED', 1980,
    DATE_SUB(NOW(), INTERVAL 35 DAY),
    DATE_SUB(NOW(), INTERVAL 35 DAY),
    NOW()
),
(
    'b0000003-0000-0000-0000-000000000016',
    'Thủ tục mua xe máy tại Đài Loan cho người nước ngoài',
    'thu-tuc-mua-xe-may-tai-dai-loan-cho-nguoi-nuoc-ngoai',
    '<h2>Có thể mua xe máy không?</h2><p>Người nước ngoài có thẻ ARC hoàn toàn có thể mua xe máy tại Đài Loan. Thủ tục khá đơn giản nếu bạn chuẩn bị đầy đủ giấy tờ.</p><h2>Giấy tờ cần thiết</h2><ul><li>Thẻ ARC còn hạn</li><li>Hộ chiếu</li><li>Bằng lái xe (đổi sang bằng Đài Loan hoặc có bằng quốc tế)</li><li>Tiền mặt hoặc thẻ ngân hàng Đài Loan</li></ul><h2>Mua mới hay cũ?</h2><p>Xe máy cũ tại Đài Loan thường còn rất tốt và giá hợp lý. Bạn có thể tìm mua trên Facebook Marketplace hoặc các group cộng đồng Việt.</p>',
    'Hướng dẫn đầy đủ thủ tục mua xe máy tại Đài Loan cho người có thẻ ARC: giấy tờ, đăng ký và bảo hiểm.',
    'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800&q=80',
    @blog_cat_exp,
    @admin_id,
    '["xe máy", "mua xe", "thủ tục", "đài loan"]',
    'PUBLISHED', 3107,
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    NOW()
);

-- ─── 4. Listings — Bất động sản (15 tin) ─────────────────────────────────────

INSERT IGNORE INTO listings (id, category_id, title, description, price, price_unit, status, featured, created_by, created_at, updated_at)
VALUES
-- Cho thuê nhà
('c0000001-0000-0000-0000-000000000001', @cat_rent_house, 'Cho thuê nhà riêng Đại Gia, 4PN, có chỗ để xe', 'Nhà riêng đường Nội Hồ, gần MRT, 4 phòng ngủ, 2 WC, sân vườn nhỏ, máy giặt. Phòng rộng thoáng mát, yên tĩnh phù hợp gia đình.', 35000, 'MONTHLY', 'ACTIVE', TRUE,  'a0000001-0000-0000-0000-000000000001', DATE_SUB(NOW(), INTERVAL 1 DAY),  NOW()),
('c0000001-0000-0000-0000-000000000002', @cat_rent_house, 'Nhà 3PN tại Xinyi gần Taipei 101, view đẹp',         'Nhà 3 tầng, 3 phòng ngủ, full nội thất, view Taipei 101 từ tầng 3. Thích hợp gia đình hoặc nhóm 3-4 người.',                            45000, 'MONTHLY', 'ACTIVE', FALSE, 'a0000001-0000-0000-0000-000000000002', DATE_SUB(NOW(), INTERVAL 2 DAY),  NOW()),
('c0000001-0000-0000-0000-000000000003', @cat_rent_house, 'Cho thuê nhà 5PN Tamsui, giá tốt, đủ đồ',           'Nhà 5 phòng ngủ tại Tamsui, cách MRT Tamsui 5 phút đi bộ, đủ đồ dùng cơ bản. Phù hợp nhóm công nhân 5-6 người.',                      20000, 'MONTHLY', 'ACTIVE', FALSE, 'a0000001-0000-0000-0000-000000000003', DATE_SUB(NOW(), INTERVAL 3 DAY),  NOW()),

-- Cho thuê phòng/apartments
('c0000001-0000-0000-0000-000000000004', @cat_rent_apt,  'Studio apartment Zhongzheng, full nội thất, 1 người', 'Studio 25m2 tầng 8, máy lạnh, máy giặt, tủ lạnh, bếp điện. Gần ga MRT Chiang Kai-shek, yên tĩnh.",',                               18000, 'MONTHLY', 'ACTIVE', TRUE,  'a0000001-0000-0000-0000-000000000001', DATE_SUB(NOW(), INTERVAL 4 DAY),  NOW()),
('c0000001-0000-0000-0000-000000000005', @cat_rent_apt,  'Căn hộ 2PN Songshan, gần Legacy Taipei',              'Căn hộ hiện đại 2 phòng ngủ, 55m2, đầy đủ nội thất cao cấp. Tầng 15, view thoáng. Gần trung tâm thương mại.',                    28000, 'MONTHLY', 'ACTIVE', FALSE, 'a0000001-0000-0000-0000-000000000004', DATE_SUB(NOW(), INTERVAL 5 DAY),  NOW()),
('c0000001-0000-0000-0000-000000000006', @cat_rent_room,  'Phòng trọ Banqiao, giá sinh viên 7000NT',             'Phòng đơn 12m2, có điều hòa, WC riêng, wifi miễn phí. Gần ĐH Quốc lập Đài Loan, phù hợp du học sinh.',                            7000,  'MONTHLY', 'ACTIVE', FALSE, 'a0000001-0000-0000-0000-000000000005', DATE_SUB(NOW(), INTERVAL 6 DAY),  NOW()),

-- Mua bán nhà đất
('c0000001-0000-0000-0000-000000000007', @cat_sale_apt,  'Bán căn hộ 3PN Da\'an, 75m2, nội thất đẹp',          'Căn hộ cao cấp quận Da\'an, 75m2, 3PN, 2WC. Tầng 12, view thoáng. Gần MRT Da\'an Park. Giá tốt so với thị trường.',              8500000, 'TOTAL',   'ACTIVE', TRUE,  'a0000001-0000-0000-0000-000000000002', DATE_SUB(NOW(), INTERVAL 7 DAY),  NOW()),
('c0000001-0000-0000-0000-000000000008', @cat_sale_house, 'Bán nhà phố Zhongshan 4 tầng, vị trí đắc địa',       'Nhà phố 4 tầng, 160m2, mặt tiền đường lớn. Thích hợp kinh doanh hoặc cho thuê. Đang cho thuê 120,000NT/tháng.',                  25000000, 'TOTAL',   'ACTIVE', FALSE, 'a0000001-0000-0000-0000-000000000003', DATE_SUB(NOW(), INTERVAL 8 DAY),  NOW());

-- listings real estate details
INSERT IGNORE INTO listing_real_estate_details (listing_id, city, district, address, area_sqm, bedrooms, bathrooms, floors, is_furnished, available_from, deposit)
VALUES
('c0000001-0000-0000-0000-000000000001', 'Taipei', 'Neihu',      '內湖路一段', 120, 4, 2, 3, TRUE,  DATE_ADD(NOW(), INTERVAL 7 DAY),  70000),
('c0000001-0000-0000-0000-000000000002', 'Taipei', 'Xinyi',      '信義路五段', 150, 3, 2, 3, TRUE,  DATE_ADD(NOW(), INTERVAL 3 DAY),  90000),
('c0000001-0000-0000-0000-000000000003', 'New Taipei', 'Tamsui', '淡水區中正路', 180, 5, 2, 2, FALSE, DATE_ADD(NOW(), INTERVAL 1 DAY),  40000),
('c0000001-0000-0000-0000-000000000004', 'Taipei', 'Zhongzheng', '中正區林森南路', 25, 0, 1, 1, TRUE,  DATE_ADD(NOW(), INTERVAL 5 DAY),  36000),
('c0000001-0000-0000-0000-000000000005', 'Taipei', 'Songshan',   '松山區南京東路', 55, 2, 1, 1, TRUE,  DATE_ADD(NOW(), INTERVAL 2 DAY),  56000),
('c0000001-0000-0000-0000-000000000006', 'New Taipei', 'Banqiao', '板橋區文化路', 12, 1, 1, 1, TRUE,  NOW(),                           14000),
('c0000001-0000-0000-0000-000000000007', 'Taipei', 'Da''an',     '大安區和平東路', 75, 3, 2, 1, TRUE,  NULL,                            NULL),
('c0000001-0000-0000-0000-000000000008', 'Taipei', 'Zhongshan',  '中山區中山北路', 160, 4, 3, 4, TRUE,  NULL,                            NULL);

-- ─── 5. Listings images ──────────────────────────────────────────────────────

INSERT IGNORE INTO listing_images (id, listing_id, url, is_cover, sort_order, created_at)
VALUES
('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001', 'https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=600&q=80', TRUE,  1, NOW()),
('d0000001-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000001', 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=600&q=80', FALSE, 2, NOW()),
('d0000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000002', 'https://images.unsplash.com/photo-1582407947304-fd86f28f2d89?w=600&q=80', TRUE,  1, NOW()),
('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000003', 'https://images.unsplash.com/photo-1484154218962-a197022b5858?w=600&q=80', TRUE,  1, NOW()),
('d0000001-0000-0000-0000-000000000005', 'c0000001-0000-0000-0000-000000000004', 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=600&q=80', TRUE,  1, NOW()),
('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000005', 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600&q=80', TRUE,  1, NOW()),
('d0000001-0000-0000-0000-000000000007', 'c0000001-0000-0000-0000-000000000006', 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&q=80', TRUE,  1, NOW()),
('d0000001-0000-0000-0000-000000000008', 'c0000001-0000-0000-0000-000000000007', 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=600&q=80', TRUE,  1, NOW()),
('d0000001-0000-0000-0000-000000000009', 'c0000001-0000-0000-0000-000000000008', 'https://images.unsplash.com/photo-1613977257363-707ba9348227?w=600&q=80', TRUE,  1, NOW());

-- ─── 6. Listings — Xe cộ (8 tin) ─────────────────────────────────────────────

INSERT IGNORE INTO listings (id, category_id, title, description, price, price_unit, status, featured, created_by, created_at, updated_at)
VALUES
('c0000002-0000-0000-0000-000000000001', @cat_car_sale,  'Toyota Corolla Cross 2022, ít đi, còn mới', 'Xe Toyota Corolla Cross bản E sản xuất 2022, đi được 18,000km, bảo dưỡng đúng hạn, không tai nạn. Màu đen, tự động. Còn bảo hành hãng.',            550000, 'TOTAL',   'ACTIVE', TRUE,  'a0000001-0000-0000-0000-000000000002', DATE_SUB(NOW(), INTERVAL 2 DAY),  NOW()),
('c0000002-0000-0000-0000-000000000002', @cat_car_sale,  'Honda CR-V 2021, 7 chỗ, full option',       'Honda CR-V 7 chỗ 2021, bản cao cấp full option, da ghế, màn hình 10 inch. Đi 25,000km, xe đẹp như mới.',                                       720000, 'TOTAL',   'ACTIVE', FALSE, 'a0000001-0000-0000-0000-000000000003', DATE_SUB(NOW(), INTERVAL 4 DAY),  NOW()),
('c0000002-0000-0000-0000-000000000003', @cat_moto_sale, 'Yamaha Gogoro 2022, pin tốt, giá tốt',     'Xe điện Gogoro 2022, pin còn 85%, đi được 12,000km. Bao gồm thẻ đổi pin Gogoro 1 năm. Đã thay má phanh.',                                          65000, 'TOTAL',   'ACTIVE', FALSE, 'a0000001-0000-0000-0000-000000000004', DATE_SUB(NOW(), INTERVAL 5 DAY),  NOW()),
('c0000002-0000-0000-0000-000000000004', @cat_moto_sale, 'SYM MAXSYM TL 508, 2020, biển số sạch',   'SYM MAXSYM TL 508cc, sản xuất 2020, đi 30,000km. Xe côn tay, đẹp, biển số không vấn đề. Phù hợp đi xa.',                                          95000, 'TOTAL',   'ACTIVE', FALSE, 'a0000001-0000-0000-0000-000000000005', DATE_SUB(NOW(), INTERVAL 6 DAY),  NOW()),
('c0000002-0000-0000-0000-000000000005', @cat_car_rent,  'Cho thuê Toyota Vios 2023, giá ngày/tuần', 'Toyota Vios 2023, 5 chỗ, số tự động. Cho thuê theo ngày: 1,800NT/ngày hoặc 10,000NT/tuần. Yêu cầu: bằng lái Đài Loan hoặc bằng quốc tế.',       1800,  'TOTAL',   'ACTIVE', TRUE,  'a0000001-0000-0000-0000-000000000001', DATE_SUB(NOW(), INTERVAL 1 DAY),  NOW()),
('c0000002-0000-0000-0000-000000000006', @cat_moto_rent, 'Cho thuê xe máy Kymco, ngắn/dài hạn',     'Xe máy Kymco Many 110cc, 2021. Thuê ngắn hạn: 600NT/ngày. Dài hạn (>1 tháng): 4,500NT/tháng. Bảo hiểm bên thứ ba đã có.',                          600,   'TOTAL',   'ACTIVE', FALSE, 'a0000001-0000-0000-0000-000000000002', DATE_SUB(NOW(), INTERVAL 3 DAY),  NOW());

-- Car details
INSERT IGNORE INTO listing_car_details (listing_id, brand, model, manufacture_year, mileage_km, fuel_type, transmission, color)
VALUES
('c0000002-0000-0000-0000-000000000001', 'Toyota', 'Corolla Cross E', 2022, 18000, 'GASOLINE', 'AUTOMATIC', 'Đen'),
('c0000002-0000-0000-0000-000000000002', 'Honda',  'CR-V L',          2021, 25000, 'GASOLINE', 'AUTOMATIC', 'Trắng ngọc trai'),
('c0000002-0000-0000-0000-000000000003', 'Yamaha', 'Gogoro 2 Plus',   2022, 12000, 'ELECTRIC',  'AUTOMATIC', 'Đỏ'),
('c0000002-0000-0000-0000-000000000004', 'SYM',    'MAXSYM TL 508',   2020, 30000, 'GASOLINE', 'MANUAL',    'Xám bạc'),
('c0000002-0000-0000-0000-000000000005', 'Toyota', 'Vios 1.5 E CVT',  2023, 5000,  'GASOLINE', 'AUTOMATIC', 'Trắng'),
('c0000002-0000-0000-0000-000000000006', 'Kymco',  'Many 110',        2021, 8000,  'GASOLINE', 'AUTOMATIC', 'Xanh dương');

-- Listing images for vehicles
INSERT IGNORE INTO listing_images (id, listing_id, url, is_cover, sort_order, created_at)
VALUES
('d0000002-0000-0000-0000-000000000001', 'c0000002-0000-0000-0000-000000000001', 'https://images.unsplash.com/photo-1619976215249-a2d4023a34ef?w=600&q=80', TRUE,  1, NOW()),
('d0000002-0000-0000-0000-000000000002', 'c0000002-0000-0000-0000-000000000002', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&q=80', TRUE,  1, NOW()),
('d0000002-0000-0000-0000-000000000003', 'c0000002-0000-0000-0000-000000000003', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&q=80', TRUE,  1, NOW()),
('d0000002-0000-0000-0000-000000000004', 'c0000002-0000-0000-0000-000000000004', 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=600&q=80', TRUE,  1, NOW()),
('d0000002-0000-0000-0000-000000000005', 'c0000002-0000-0000-0000-000000000005', 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=600&q=80', TRUE,  1, NOW()),
('d0000002-0000-0000-0000-000000000006', 'c0000002-0000-0000-0000-000000000006', 'https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=600&q=80', TRUE,  1, NOW());

-- ─── 7. Donate account mẫu ──────────────────────────────────────────────────

INSERT IGNORE INTO donation_accounts (id, bank_name, account_number, account_holder, qr_image_url, active, sort_order, created_at, updated_at)
VALUES
('d4000001-0000-0000-0000-000000000001', 'E.SUN Bank (玉山銀行)', '808-123-456789', 'NGUYEN THANH PHUOC', 'https://upload.wikimedia.org/wikipedia/commons/d/d0/QR_code_for_mobile_English_Wikipedia.svg', 1, 1, NOW(6), NOW(6));
