-- Add verified column to users table
ALTER TABLE users ADD COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE;

-- Create otp_verifications table
CREATE TABLE otp_verifications (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    phone_number    VARCHAR(20)     NOT NULL,
    otp_code        VARCHAR(6)      NOT NULL,
    expires_at      DATETIME        NOT NULL,
    verified        BOOLEAN         NOT NULL DEFAULT FALSE,
    resend_count    INT             NOT NULL DEFAULT 0,
    verify_attempts INT             NOT NULL DEFAULT 0,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_otp_phone_number ON otp_verifications (phone_number);
