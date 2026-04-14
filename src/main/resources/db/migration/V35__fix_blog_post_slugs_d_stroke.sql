-- V35: Fix blog post slugs where Vietnamese 'đ'/'Đ' (U+0110/U+0111) was stripped
-- instead of converted to 'd'. Root cause: Normalizer.Form.NFD does not decompose
-- U+0111 (LATIN LETTER D WITH STROKE), so it was removed by the NON_ASCII regex.
-- Fixed in SlugUtils.java by pre-replacing 'đ' -> 'd' before NFD normalization.

-- "Mua Sắm Đồ Cũ Tại Đài Loan..."  (Đồ→o, Đài→ai)
UPDATE blog_posts
SET slug = 'mua-sam-do-cu-tai-dai-loan-tiet-kiem-thong-minh-cho-nguoi-viet-moi-sang'
WHERE slug = 'mua-sam-o-cu-tai-ai-loan-tiet-kiem-thong-minh-cho-nguoi-viet-moi-sang';

-- "Dịch Vụ Vận Chuyển Hàng Hóa Đài Loan..."  (Đài→ai)
UPDATE blog_posts
SET slug = 'dich-vu-van-chuyen-hang-hoa-dai-loan-viet-nam-gui-hang-an-toan-tiet-kiem'
WHERE slug = 'dich-vu-van-chuyen-hang-hoa-ai-loan-viet-nam-gui-hang-an-toan-tiet-kiem';

-- "Hướng Dẫn Mở Tài Khoản Ngân Hàng Tại Đài Loan..."  (Đài→ai)
UPDATE blog_posts
SET slug = 'huong-dan-mo-tai-khoan-ngan-hang-tai-dai-loan-cho-nguoi-nuoc-ngoai'
WHERE slug = 'huong-dan-mo-tai-khoan-ngan-hang-tai-ai-loan-cho-nguoi-nuoc-ngoai';

-- "Bảo Hiểm Lao Động Tại Đài Loan..."  (Động→ong, Đài→ai)
UPDATE blog_posts
SET slug = 'bao-hiem-lao-dong-tai-dai-loan-quyen-loi-nguoi-viet-can-biet'
WHERE slug = 'bao-hiem-lao-ong-tai-ai-loan-quyen-loi-nguoi-viet-can-biet';

-- "Chuyển Tiền Từ Đài Loan Về Việt Nam..."  (Đài→ai)
UPDATE blog_posts
SET slug = 'chuyen-tien-tu-dai-loan-ve-viet-nam-cach-an-toan-va-tiet-kiem-nhat'
WHERE slug = 'chuyen-tien-tu-ai-loan-ve-viet-nam-cach-an-toan-va-tiet-kiem-nhat';

-- "5 Điểm Du Lịch Cuối Tuần Tuyệt Vời Gần Đài Bắc..."  (Điểm→iem, Đài→ai)
UPDATE blog_posts
SET slug = '5-diem-du-lich-cuoi-tuan-tuyet-voi-gan-dai-bac-danh-cho-nguoi-viet'
WHERE slug = '5-iem-du-lich-cuoi-tuan-tuyet-voi-gan-ai-bac-danh-cho-nguoi-viet';

-- "Ẩm Thực Việt Nam Tại Đài Loan..."  (Đài→ai)
UPDATE blog_posts
SET slug = 'am-thuc-viet-nam-tai-dai-loan-quan-an-cua-hang-va-meo-nau-an'
WHERE slug = 'am-thuc-viet-nam-tai-ai-loan-quan-an-cua-hang-va-meo-nau-an';

-- "Kinh Nghiệm Mua Xe Máy Cũ Tại Đài Loan..."  (Đài→ai)
UPDATE blog_posts
SET slug = 'kinh-nghiem-mua-xe-may-cu-tai-dai-loan-thu-tuc-gia-va-nhung-luu-y'
WHERE slug = 'kinh-nghiem-mua-xe-may-cu-tai-ai-loan-thu-tuc-gia-va-nhung-luu-y';

-- "Top 5 Ngành Nghề Đang Tuyển Dụng Nhiều Nhất Tại Đài Loan..."  (Đang→ang, Đài→ai)
UPDATE blog_posts
SET slug = 'top-5-nganh-nghe-dang-tuyen-dung-nhieu-nhat-tai-dai-loan-nam-2026'
WHERE slug = 'top-5-nganh-nghe-ang-tuyen-dung-nhieu-nhat-tai-ai-loan-nam-2026';

-- "Hướng Dẫn Đăng Ký ARC Tại Đài Loan..."  (Đăng→ang, Đài→ai)
UPDATE blog_posts
SET slug = 'huong-dan-dang-ky-arc-the-cu-tru-tai-dai-loan-cho-nguoi-viet'
WHERE slug = 'huong-dan-ang-ky-arc-the-cu-tru-tai-ai-loan-cho-nguoi-viet';
