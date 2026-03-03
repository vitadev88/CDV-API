-- Drop FK constraint before altering columns
ALTER TABLE listings DROP FOREIGN KEY fk_listings_created_by;

-- Clear existing data (dev environment)
TRUNCATE TABLE listings;
TRUNCATE TABLE users;

-- Change users.id from BIGINT AUTO_INCREMENT to CHAR(36)
ALTER TABLE users MODIFY id CHAR(36) NOT NULL;

-- Change listings.id and created_by to CHAR(36)
ALTER TABLE listings MODIFY id CHAR(36) NOT NULL;
ALTER TABLE listings MODIFY created_by CHAR(36) NOT NULL;

-- Re-add FK constraint
ALTER TABLE listings ADD CONSTRAINT fk_listings_created_by FOREIGN KEY (created_by) REFERENCES users(id);
