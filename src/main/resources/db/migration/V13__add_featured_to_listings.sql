ALTER TABLE listings
    ADD COLUMN featured BOOLEAN NOT NULL DEFAULT FALSE AFTER status;

CREATE INDEX idx_listings_featured ON listings (featured);
