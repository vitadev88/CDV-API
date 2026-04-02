ALTER TABLE users
    ADD COLUMN avatar_url VARCHAR(500) NULL AFTER phone,
    ADD COLUMN bio VARCHAR(500) NULL AFTER avatar_url,
    ADD COLUMN address VARCHAR(255) NULL AFTER bio,
    ADD COLUMN date_of_birth DATE NULL AFTER address;
