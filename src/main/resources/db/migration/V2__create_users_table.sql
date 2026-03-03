CREATE TABLE users (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    full_name   VARCHAR(255),
    phone       VARCHAR(20),
    role        VARCHAR(50)     NOT NULL DEFAULT 'USER',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role ON users (role);

