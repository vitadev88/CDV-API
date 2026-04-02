CREATE TABLE feedbacks (
    id          CHAR(36)     NOT NULL PRIMARY KEY,
    content     TEXT         NOT NULL,
    category    VARCHAR(20)  NOT NULL DEFAULT 'OTHER',
    sender_name VARCHAR(100) NULL,
    sender_email VARCHAR(150) NULL,
    user_id     CHAR(36)     NULL,
    status      VARCHAR(10)  NOT NULL DEFAULT 'NEW',
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NULL,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
