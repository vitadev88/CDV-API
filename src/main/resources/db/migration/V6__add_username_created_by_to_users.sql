-- Allow email to be nullable: SUPER_ADMIN accounts are system-level and need no email
ALTER TABLE users MODIFY COLUMN email VARCHAR(255) NULL;

-- username: used as login identifier for SUPER_ADMIN (and future admin accounts)
ALTER TABLE users ADD COLUMN username VARCHAR(100) NULL;
ALTER TABLE users ADD CONSTRAINT uq_users_username UNIQUE (username);

-- created_by: audit trail for who/what created this account
ALTER TABLE users ADD COLUMN created_by VARCHAR(100) NULL;

CREATE INDEX idx_users_username ON users (username);
