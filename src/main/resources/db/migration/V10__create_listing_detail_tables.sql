-- -------------------------------------------------------
-- Real estate details (dùng cho cả cho thuê lẫn mua bán BĐS)
-- listing_id là PK đồng thời là FK tới listings.id
-- -------------------------------------------------------
CREATE TABLE listing_real_estate_details (
    listing_id      CHAR(36)        NOT NULL,
    city            VARCHAR(100)    NOT NULL,
    district        VARCHAR(100),
    address         VARCHAR(255),
    area_sqm        DECIMAL(8, 2),
    bedrooms        INT,
    bathrooms       INT,
    floors          INT,
    is_furnished    BOOLEAN         NOT NULL DEFAULT FALSE,
    -- Dùng cho cho thuê
    available_from  DATE,
    deposit         DECIMAL(15, 2),
    PRIMARY KEY (listing_id),
    CONSTRAINT fk_re_details_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_re_details_city ON listing_real_estate_details (city);

-- -------------------------------------------------------
-- Car details
-- -------------------------------------------------------
CREATE TABLE listing_car_details (
    listing_id          CHAR(36)        NOT NULL,
    brand               VARCHAR(100)    NOT NULL,
    model               VARCHAR(100)    NOT NULL,
    manufacture_year    INT             NOT NULL,
    mileage_km          INT,
    fuel_type           VARCHAR(20),    -- GASOLINE | DIESEL | ELECTRIC | HYBRID
    transmission        VARCHAR(20),    -- MANUAL | AUTOMATIC
    color               VARCHAR(50),
    PRIMARY KEY (listing_id),
    CONSTRAINT fk_car_details_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_car_details_brand ON listing_car_details (brand);
CREATE INDEX idx_car_details_year  ON listing_car_details (manufacture_year);
