ALTER TABLE users
    ADD COLUMN zalo     VARCHAR(100) NULL AFTER date_of_birth,
    ADD COLUMN line_id  VARCHAR(100) NULL AFTER zalo,
    ADD COLUMN facebook VARCHAR(255) NULL AFTER line_id,
    ADD COLUMN tiktok   VARCHAR(255) NULL AFTER facebook;
