ALTER TABLE app_settings
    ADD COLUMN logo_url VARCHAR(500) NULL,
    ADD COLUMN site_title VARCHAR(255) NOT NULL DEFAULT 'CongDongViet - Cộng đồng Việt Nam tại Đài Loan',
    ADD COLUMN site_description TEXT NULL,
    ADD COLUMN maintenance_mode BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE app_settings
SET site_description = 'Nền tảng kết nối, hỗ trợ và chia sẻ thông tin hữu ích cho người Việt đang sinh sống, làm việc và học tập tại Đài Loan.'
WHERE id = 1;
